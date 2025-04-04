package com.receipts.receipt_sharing.presentation.recipes.recipePage

import RecipesRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.receipts.receipt_sharing.data.helpers.FileHelper
import com.receipts.receipt_sharing.data.repositoriesImpl.AuthDataStoreRepository
import com.receipts.receipt_sharing.domain.recipes.Ingredient
import com.receipts.receipt_sharing.domain.recipes.Measure
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.recipes.Step
import com.receipts.receipt_sharing.domain.repositories.CreatorsRepository
import com.receipts.receipt_sharing.domain.repositories.FiltersRepository
import com.receipts.receipt_sharing.domain.repositories.ReviewsRepository
import com.receipts.receipt_sharing.domain.response.ApiResult
import com.receipts.receipt_sharing.domain.reviews.ReviewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File


class RecipePageViewModel(
    private val recipesRepo: RecipesRepository,
    private val filtersRepo: FiltersRepository,
    private val reviewsRepo: ReviewsRepository,
    private val creatorsRepo: CreatorsRepository
) : ViewModel() {

    private val fileHelper = FileHelper.get()
    private val authDataStoreRepo = AuthDataStoreRepository.get()
    private val _selectedRecipe = MutableStateFlow<ApiResult<Recipe>>(ApiResult.Downloading())
    private val _loadedFilters =
        MutableStateFlow<ApiResult<Map<String, List<String>>>>(ApiResult.Downloading())

    private val _state = MutableStateFlow(RecipePageState())

    val state = combine(_state, _selectedRecipe, _loadedFilters) { state, selectedRecipe, filters ->
        state.copy(
            recipe = selectedRecipe,
            loadedFilters = filters
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), RecipePageState())

    /**
     * Processes Recipe info page events
     * @param event Recipe info page event
     * @see [RecipePageEvent]
     */
    fun onEvent(event: RecipePageEvent) {
        viewModelScope.launch {
            when (event) {
                is RecipePageEvent.AddIngredient -> {
                    _state.update {
                        it.copy(
                            ingredients = state.value.ingredients.plus(event.ingredient)
                        )
                    }
                }

                RecipePageEvent.LoadAllFilters -> {
                    _loadedFilters.update {
                        authDataStoreRepo.authDataStoreFlow.first().token?.let { tok ->
                            filtersRepo.getCategorizedFilters(tok)
                        } ?: ApiResult.Error("Unauthorized")
                    }
                }

                is RecipePageEvent.AddStep -> {
                    _state.update {
                        it.copy(
                            steps = state.value.steps.plus(event.step)
                        )
                    }
                }

                is RecipePageEvent.LoadRecipe -> {
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    _selectedRecipe.update {
                        ApiResult.Downloading()
                    }
                    _selectedRecipe.update {
                        token?.let {
                            recipesRepo.getRecipeByID(it, event.recipeID)
                        } ?: ApiResult.Error("Unauthorized")
                    }
                    val filters = if (!token.isNullOrEmpty()) filtersRepo.getFiltersByRecipe(
                        token,
                        event.recipeID
                    ) else ApiResult.Error("Unauthorized")


                    val owns = token?.let {
                        recipesRepo.isRecipeOwn(it, event.recipeID)
                    }
                    val isFavorite = token?.let {
                        recipesRepo.isRecipeInFavorites(it, event.recipeID)
                    }
                    val recipeRating = token?.let {
                        reviewsRepo.getRecipeRating(token, event.recipeID).data?.toFloat()
                    } ?: 0f
                    val reviewsCount = token?.let {
                        reviewsRepo.getReviewsCountByRecipe(token, event.recipeID).data
                    } ?: 0

                    _state.update {
                        it.copy(
                            recipeName = state.value.recipe.data?.recipeName ?: "",
                            recipeDescription = state.value.recipe.data?.description ?: "",
                            imageUrl = state.value.recipe.data?.imageUrl,
                            recipeRating = recipeRating,
                            recipeReviewsCount = reviewsCount,
                            own = owns?.data ?: false,
                            isEditingRecord = false,
                            filters = filters,
                            isFavorite = isFavorite?.data ?: false,
                            ingredients = state.value.recipe.data?.ingredients ?: emptyList(),
                            steps = state.value.recipe.data?.steps ?: emptyList(),
                            reviews = ApiResult.Downloading(),
                            selectedRecipeTabIndex = 0,
                            openFiltersPage = false,
                            openAddIngredientDialog = false,
                            openAddStepDialog = false,
                            openConfirmDeleteDialog = false
                        )
                    }

                    launch {
                        val reviews = token?.let { tok ->
                            reviewsRepo.getTopReviewsByRecipe(tok, event.recipeID)
                        }
                        val models = when (reviews) {
                            is ApiResult.Downloading -> ApiResult.Downloading()
                            is ApiResult.Error -> ApiResult.Error(reviews.info)
                            is ApiResult.Succeed -> ApiResult.Succeed(reviews.data?.mapNotNull {
                                creatorsRepo.getCreatorById(token, it.userID).data?.let { creator ->
                                    ReviewModel(
                                        id = it._id,
                                        userName = creator.nickname,
                                        userImageUrl = creator.imageUrl,
                                        text = it.text,
                                        rating = it.rating
                                    )
                                }
                            })

                            null -> ApiResult.Error("Illegal token")
                        }
                        _state.update {
                            it.copy(reviews = models)
                        }
                    }
                }

                is RecipePageEvent.RemoveIngredient -> {
                    _state.update {
                        it.copy(
                            ingredients = state.value.ingredients.minus(event.ingredient)
                        )
                    }
                }

                is RecipePageEvent.RemoveStep -> {
                    _state.update {
                        it.copy(
                            steps = state.value.steps.minus(event.step)
                        )
                    }
                }

                RecipePageEvent.SaveChanges -> {
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    val name = state.value.recipeName
                    val desc = state.value.recipeDescription
                    val ingr = state.value.ingredients
                    val steps = state.value.steps
                    val imageUrl = state.value.imageUrl
                    val filters = state.value.filters.data
                    if (name.isBlank() || ingr.isEmpty() || steps.isEmpty()) {
                        _state.update {
                            it.copy(
                                infoMessage = "Error!! Don`t leave required fields empty"
                            )
                        }
                        return@launch
                    }
                    var recipe = state.value.recipe.data?.copy(
                        recipeName = name,
                        description = desc,
                        ingredients = ingr,
                        steps = steps,
                        imageUrl = imageUrl
                    )
                    if (!token.isNullOrEmpty() && recipe != null && state.value.own) {
                        val updateID = if (recipe.recipeID.isEmpty())
                            recipesRepo.postRecipe(token, recipe).data
                        else {
                            recipesRepo.updateRecipe(token, recipe)
                            recipe.recipeID
                        }

                        updateID?.let {
                            if (!filters.isNullOrEmpty()) {
                                filtersRepo.attachFiltersToRecipe(token, updateID, filters)
                            }
                            _selectedRecipe.update {
                                ApiResult.Succeed(recipe.copy(recipeID = updateID))
                            }
                        }

                        _state.update {
                            it.copy(
                                isEditingRecord = false,
                            )
                        }
                    }
                }

                RecipePageEvent.DeleteRecipe -> {
                    authDataStoreRepo.authDataStoreFlow.first().token?.let { tok ->
                        if (state.value.recipe.data == null || state.value.recipe.data!!.recipeID.isEmpty())
                            _state.update {
                                it.copy(infoMessage = "Recipe was not created")
                            }
                        else {
                            val result =
                                recipesRepo.deleteRecipe(tok, state.value.recipe.data!!.recipeID)
                            _state.update {
                                when (result) {
                                    is ApiResult.Succeed -> it.copy(
                                        imageUrl = null,
                                        recipeName = "",
                                        recipeRating = 0f,
                                        recipeDescription = "",
                                        own = false,
                                        recipeReviewsCount = 0,
                                        isFavorite = false,
                                        steps = emptyList(),
                                        ingredients = emptyList(),
                                        filters = ApiResult.Downloading(),
                                        reviews = ApiResult.Downloading(),
                                        isEditingRecord = false,
                                        openAddStepDialog = false,
                                        openAddIngredientDialog = false,
                                        openConfirmDeleteDialog = false,
                                        isError = false,
                                    )

                                    else -> it.copy(infoMessage = result.info ?: "Unknown error")
                                }
                            }
                        }
                    }
                }

                is RecipePageEvent.SetRecipeDescription -> _state.update {
                    it.copy(recipeDescription = event.receiptDescription)
                }

                is RecipePageEvent.SetRecipeName -> _state.update {
                    it.copy(recipeName = event.receiptName)
                }

                is RecipePageEvent.SetImageUrl -> {
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    if (token != null) {
                        val file = fileHelper.getFileFromUri(event.imageUri)
                        file?.let { imageFilePath ->
                            _state.update {
                                it.copy(
                                    imageUrl = recipesRepo.uploadRecipeImage(
                                        token,
                                        File(imageFilePath)
                                    ).data
                                )
                            }
                        }
                    }
                }

                is RecipePageEvent.UpdateIngredient -> _state.update {
                    it.copy(
                        ingredients = state.value.ingredients.toMutableList().apply {
                            set(event.index, event.ingredient)
                            toList()
                        }
                    )
                }

                is RecipePageEvent.UpdateStep -> _state.update {
                    it.copy(
                        steps = state.value.steps.toMutableList().apply {
                            set(event.index, event.step)
                            toList()
                        }
                    )
                }

                is RecipePageEvent.ChangeIsFavorite -> {
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    if (token != null) {
                        state.value.recipe.data?.let { recipe ->
                            if (state.value.isFavorite) {
                                recipesRepo.removeFromFavorites(
                                    token, recipe.recipeID
                                )
                                _state.update {
                                    it.copy(
                                        isFavorite = !state.value.isFavorite
                                    )
                                }
                            } else {
                                recipesRepo.addToFavorites(
                                    token, recipe.recipeID
                                )
                                _state.update {
                                    it.copy(
                                        isFavorite = !state.value.isFavorite
                                    )
                                }
                            }
                        } ?: _state.update {
                            it.copy(infoMessage = "Error!! Cannot find info about loaded recipe.")
                        }
                    } else _state.update {
                        it.copy(infoMessage = "Error!! Cannot find account info")
                    }
                }

                RecipePageEvent.InitializeRecipe -> {
                    _selectedRecipe.update {
                        ApiResult.Succeed(
                            Recipe("", "", null, "", "", emptyList(), emptyList(), 0f, 0L, 0L)
                        )
                    }
                    _state.update {
                        it.copy(
                            imageUrl = null,
                            recipeName = "",
                            ingredients = emptyList(),
                            steps = emptyList(),
                            recipeDescription = "",
                            own = true,
                            isEditingRecord = true,
                            openConfirmDeleteDialog = false,
                            openAddStepDialog = false,
                            openAddIngredientDialog = false,
                            recipeRating = 0f,
                            recipeReviewsCount = 0,
                            filters = ApiResult.Succeed(emptyList()),
                            reviews = ApiResult.Succeed(emptyList()),
                        )
                    }
                }

                is RecipePageEvent.SetFilters -> _state.update {
                    it.copy(
                        filters = ApiResult.Succeed(event.filters)
                    )
                }

                RecipePageEvent.CloseDialogs -> _state.update {
                    it.copy(
                        openAddIngredientDialog = false,
                        openAddStepDialog = false,
                        openConfirmDeleteDialog = false,
                        selectedIngredient = Ingredient("", 0f, Measure.Gram),
                        selectedStep = Step("", 0L),
                        selectedStepIndex = -1,
                        selectedIngrIndex = -1
                    )
                }

                RecipePageEvent.EditRecord -> _state.update {
                    it.copy(isEditingRecord = true)
                }

                RecipePageEvent.DiscardChanges -> {
                    _state.update {
                        _selectedRecipe.value.data?.let { recipe ->
                            val token = authDataStoreRepo.authDataStoreFlow.first().token

                            val filters =
                                if (!token.isNullOrEmpty()) filtersRepo.getFiltersByRecipe(
                                    token,
                                    recipe.recipeID
                                ) else ApiResult.Error()

                            it.copy(
                                recipeName = state.value.recipe.data?.recipeName ?: "",
                                recipeDescription = state.value.recipe.data?.description ?: "",
                                isEditingRecord = false,
                                filters = filters,
                                ingredients = state.value.recipe.data?.ingredients ?: emptyList(),
                                steps = state.value.recipe.data?.steps ?: emptyList(),
                                openAddIngredientDialog = false,
                                openAddStepDialog = false,
                                openConfirmDeleteDialog = false,
                                openFiltersPage = false
                            )
                        } ?: it.copy(
                            infoMessage = "Error!! Cannot find info about recipe! Try reloading the page.",
                            isEditingRecord = false
                        )
                    }
                }


                is RecipePageEvent.SetOpenIngredientConfigDialog -> _state.update {
                    it.copy(openAddIngredientDialog = event.openDialog)
                }

                is RecipePageEvent.SetOpenStepConfigDialog -> _state.update {
                    it.copy(openAddStepDialog = event.openDialog)
                }

                is RecipePageEvent.SetOpenFiltersPage -> {
                    _state.update {
                        it.copy(openFiltersPage = event.openDialog)
                    }
                    if (event.openDialog)
                        onEvent(RecipePageEvent.LoadAllFilters)
                }

                is RecipePageEvent.SetSelectedIngredient -> _state.update {
                    it.copy(
                        selectedIngredient = event.ingredient,
                        selectedIngrIndex = state.value.ingredients.indexOf(event.ingredient)
                    )
                }

                is RecipePageEvent.SetSelectedStep -> _state.update {
                    it.copy(
                        selectedStep = event.step,
                        selectedStepIndex = state.value.steps.indexOf(event.step)
                    )
                }

                is RecipePageEvent.SetSelectedRecipeTabIndex -> {
                    _state.update {
                        it.copy(selectedRecipeTabIndex = event.recipeTabIndex)
                    }
                }

                is RecipePageEvent.SetIsError -> {
                    _state.update {
                        it.copy(isError = event.isError)
                    }
                }

                RecipePageEvent.OpenConfirmDeleteDialog -> _state.update {
                    it.copy(openConfirmDeleteDialog = true)
                }
            }
        }
    }
}
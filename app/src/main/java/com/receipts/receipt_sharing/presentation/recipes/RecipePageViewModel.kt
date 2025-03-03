package com.receipts.receipt_sharing.presentation.recipes

import IRecipesRepository
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.receipts.receipt_sharing.data.helpers.FileHelper
import com.receipts.receipt_sharing.data.repositoriesImpl.AuthDataStoreRepository
import com.receipts.receipt_sharing.domain.recipes.Ingredient
import com.receipts.receipt_sharing.domain.recipes.Measure
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.recipes.Step
import com.receipts.receipt_sharing.domain.repositories.ICreatorsRepository
import com.receipts.receipt_sharing.domain.repositories.IFiltersRepository
import com.receipts.receipt_sharing.domain.repositories.IReviewsRepository
import com.receipts.receipt_sharing.domain.response.RecipeResult
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
    private val recipesRepo: IRecipesRepository,
    private val filtersRepo: IFiltersRepository,
    private val reviewsRepo: IReviewsRepository,
    private val creatorsRepo: ICreatorsRepository
) : ViewModel() {

    private val fileHelper = FileHelper.get()
    private val authDataStoreRepo = AuthDataStoreRepository.get()
    private val _selectedRecipe = MutableStateFlow<RecipeResult<Recipe>>(RecipeResult.Downloading())

    private val _state = MutableStateFlow(RecipePageState())

    val state = combine(_state, _selectedRecipe) { state, selectedRecipe ->
        state.copy(
            recipe = selectedRecipe
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), RecipePageState())

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
                        RecipeResult.Downloading()
                    }
                    _selectedRecipe.update {
                        token?.let {
                            recipesRepo.getRecipeByID(it, event.recipeID)
                        } ?: RecipeResult.Error("Unauthorized")
                    }
                    val filters = if (!token.isNullOrEmpty()) filtersRepo.getFiltersByRecipe(
                        token,
                        event.recipeID
                    ) else RecipeResult.Error()


                    val owns = token?.let {
                        recipesRepo.isRecipeOwn(it, event.recipeID)
                    }
                    val isFavorite = token?.let {
                        recipesRepo.isRecipeInFavorites(it, event.recipeID)
                    }

                    _state.update {
                        it.copy(
                            recipeName = state.value.recipe.data?.recipeName ?: "",
                            recipeDescription = state.value.recipe.data?.description ?: "",
                            own = owns?.data ?: false,
                            filters = filters,
                            isFavorite = isFavorite?.data ?: false,
                            ingredients = state.value.recipe.data?.ingredients ?: emptyList(),
                            steps = state.value.recipe.data?.steps ?: emptyList(),
                            reviews = RecipeResult.Downloading()
                        )
                    }

                    launch {
                        val reviews = token?.let { tok ->
                            reviewsRepo.getReviewsByRecipe(tok, event.recipeID)
                        }
                        val models = when (reviews) {
                            is RecipeResult.Downloading -> RecipeResult.Downloading()
                            is RecipeResult.Error -> RecipeResult.Error(reviews.info)
                            is RecipeResult.Succeed -> RecipeResult.Succeed(reviews.data?.mapNotNull {
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

                            null -> RecipeResult.Error("Illegal token")
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
                    if (name.isBlank() || desc.isBlank() || ingr.isEmpty() || steps.isEmpty()) {
                        _state.update {
                            it.copy(
                                infoMessage = "Error!! Don`t leave required fields empty"
                            )
                        }
                        return@launch
                    }
                    val recipe = state.value.recipe.data?.copy(
                        recipeName = name,
                        description = desc,
                        ingredients = ingr,
                        steps = steps,
                        imageUrl = imageUrl.data
                    )
                    if (!token.isNullOrEmpty() && recipe != null && state.value.own) {
                        var updateID = if (recipe.recipeID.isEmpty())
                            recipesRepo.postRecipe(token, recipe).data
                        else {
                            recipesRepo.updateRecipe(token, recipe)
                            recipe.recipeID
                        }

                        if (updateID != null && !filters.isNullOrEmpty()) {
                            filtersRepo.attachFiltersToRecipe(token, updateID, filters)
                        }
                        _state.update {
                            it.copy(
                                isEditingRecord = false
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
                                    is RecipeResult.Succeed -> it.copy(infoMessage = "Successfully deleted")
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
                                    imageUrl = RecipeResult.Downloading()
                                )
                            }
                            _state.update {
                                it.copy(
                                    imageUrl = recipesRepo.uploadRecipeImage(
                                        token,
                                        File(imageFilePath)
                                    )
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

                RecipePageEvent.ClearRecipeData -> {
                    _selectedRecipe.update {
                        RecipeResult.Succeed(
                            Recipe("", "", null, "", "", emptyList(), emptyList())
                        )
                    }
                    _state.update {
                        it.copy(
                            imageUrl = RecipeResult.Succeed(),
                            recipeName = "",
                            ingredients = emptyList(),
                            steps = emptyList(),
                            recipeDescription = "",
                            own = true,
                            isEditingRecord = true
                        )
                    }
                }

                is RecipePageEvent.SetFilters -> _state.update {
                    it.copy(
                        filters = RecipeResult.Succeed(event.filters)
                    )
                }

                RecipePageEvent.CloseDialogs -> _state.update {
                    it.copy(
                        openAddIngredientDialog = false,
                        openAddStepDialog = false,
                        selectedIngredient = Ingredient("", 0L, Measure.Gram),
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
                                ) else RecipeResult.Error()


                            val owns = token?.let {
                                recipesRepo.isRecipeOwn(it, recipe.recipeID)
                            }
                            val isFavorite = token?.let {
                                recipesRepo.isRecipeInFavorites(it, recipe.recipeID)
                            }
                            it.copy(
                                recipeName = state.value.recipe.data?.recipeName ?: "",
                                recipeDescription = state.value.recipe.data?.description ?: "",
                                own = owns?.data ?: false,
                                filters = filters,
                                isFavorite = isFavorite?.data ?: false,
                                ingredients = state.value.recipe.data?.ingredients ?: emptyList(),
                                steps = state.value.recipe.data?.steps ?: emptyList()
                            )
                        } ?: it.copy(
                            infoMessage = "Error!! Cannot find info about recipe! Try reloading the page."
                        )
                    }
                }

                is RecipePageEvent.SetOpenIngredientConfigDialog -> _state.update {
                    it.copy(openAddIngredientDialog = event.openDialog)
                }

                is RecipePageEvent.SetOpenStepConfigDialog -> _state.update {
                    it.copy(openAddStepDialog = event.openDialog)
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


sealed interface RecipePageEvent {
    //Recipe
    data class LoadRecipe(val recipeID: String) : RecipePageEvent
    data object EditRecord : RecipePageEvent
    data object DiscardChanges : RecipePageEvent
    data object DeleteRecipe : RecipePageEvent
    data class SetRecipeName(val receiptName: String) : RecipePageEvent
    data class SetRecipeDescription(val receiptDescription: String) : RecipePageEvent
    data object ChangeIsFavorite : RecipePageEvent
    data object ClearRecipeData : RecipePageEvent
    data class SetFilters(val filters: List<String>) : RecipePageEvent

    //Ingredients
    data class AddIngredient(val ingredient: Ingredient) : RecipePageEvent
    data class UpdateIngredient(val index: Int, val ingredient: Ingredient) : RecipePageEvent
    data class RemoveIngredient(val ingredient: Ingredient) : RecipePageEvent
    data class SetSelectedIngredient(
        val ingredient: Ingredient = Ingredient(
            "",
            0L,
            Measure.Gram
        )
    ) : RecipePageEvent

    //Steps
    data class SetImageUrl(val imageUri: Uri?) : RecipePageEvent
    data class AddStep(val step: Step) : RecipePageEvent
    data class RemoveStep(val step: Step) : RecipePageEvent
    data class UpdateStep(val index: Int, val step: Step) : RecipePageEvent
    data class SetSelectedStep(val step: Step = Step("", 0L)) : RecipePageEvent

    //Page operations
    data object SaveChanges : RecipePageEvent
    data class SetOpenIngredientConfigDialog(val openDialog: Boolean) : RecipePageEvent
    data class SetOpenStepConfigDialog(val openDialog: Boolean) : RecipePageEvent
    data object OpenConfirmDeleteDialog : RecipePageEvent
    data object CloseDialogs : RecipePageEvent
    data class SetSelectedRecipeTabIndex(val recipeTabIndex: Int) : RecipePageEvent
    data class SetIsError(val isError: Boolean) : RecipePageEvent
}
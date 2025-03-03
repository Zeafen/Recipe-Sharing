package com.receipts.receipt_sharing.data.viewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.receipts.receipt_sharing.data.repositoriesImpl.AuthDataStoreRepository
import com.receipts.receipt_sharing.data.repositoriesImpl.FiltersRepositoryImpl
import com.receipts.receipt_sharing.data.repositoriesImpl.RecipesRepositoryImpl
import com.receipts.receipt_sharing.domain.helpers.FileHelper
import com.receipts.receipt_sharing.domain.recipes.Ingredient
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.recipes.Step
import com.receipts.receipt_sharing.domain.response.RecipeResult
import com.receipts.receipt_sharing.ui.recipe.RecipePageState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

private const val TAG = "recipePageVM"

@HiltViewModel
class RecipePageViewModel @Inject constructor(
    private val recipesRepo: RecipesRepositoryImpl,
    private val filtersRepo: FiltersRepositoryImpl
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
        when (event) {
            is RecipePageEvent.AddIngredient -> viewModelScope.launch {
                _state.update {
                    it.copy(
                        ingredients = state.value.ingredients.plus(event.ingredient)
                    )
                }
            }

            is RecipePageEvent.AddStep -> viewModelScope.launch {
                _state.update {
                    it.copy(
                        steps = state.value.steps.plus(event.step)
                    )
                }
            }

            is RecipePageEvent.LoadRecipe -> viewModelScope.launch {
                val token = authDataStoreRepo.authDataStoreFlow.first().token
                _selectedRecipe.update {
                    RecipeResult.Downloading()
                }
                _selectedRecipe.update {
                    token?.let {
                        recipesRepo.getRecipeByID(it, event.receiptId)
                    } ?: RecipeResult.Error("Unauthorized")
                }
                val filters = if (!token.isNullOrEmpty()) filtersRepo.getFiltersByRecipe(
                    token,
                    event.receiptId
                ) else RecipeResult.Error()


                val owns = token?.let {
                    recipesRepo.isRecipeOwn(it, event.receiptId)
                }
                val isFavorite = token?.let {
                    recipesRepo.isRecipeInFavorites(it, event.receiptId)
                }
                _state.update {
                    it.copy(
                        recipeName = state.value.recipe.data?.recipeName ?: "",
                        recipeDescription = state.value.recipe.data?.description ?: "",
                        own = owns?.data ?: false,
                        filters = filters,
                        isFavorite = isFavorite?.data ?: false,
                        ingredients = state.value.recipe.data?.ingredients ?: emptyList(),
                        steps = state.value.recipe.data?.steps ?: emptyList()
                    )
                }
            }

            is RecipePageEvent.RemoveIngredient -> viewModelScope.launch {
                _state.update {
                    it.copy(
                        ingredients = state.value.ingredients.minus(event.ingredient)
                    )
                }
            }

            is RecipePageEvent.RemoveStep -> viewModelScope.launch {
                _state.update {
                    it.copy(
                        steps = state.value.steps.minus(event.step)
                    )
                }
            }

            RecipePageEvent.SaveChanges -> viewModelScope.launch {
                val token = authDataStoreRepo.authDataStoreFlow.first().token
                val name = state.value.recipeName
                val desc = state.value.recipeDescription
                val ingr = state.value.ingredients
                val steps = state.value.steps
                val imageUrl = state.value.imageUrl
                val filters = state.value.filters.data
                if (name.isBlank() || desc.isBlank() || ingr.isEmpty() || steps.isEmpty())
                    return@launch
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
                    else{
                        recipesRepo.updateRecipe(token, recipe)
                        recipe.recipeID
                    }

                    if(updateID != null && !filters.isNullOrEmpty()){
                        filtersRepo.attachFiltersToRecipe(token, updateID, filters)
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
                viewModelScope.launch {
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

            is RecipePageEvent.AddToFavourites -> {
                viewModelScope.launch {
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    if (token != null) {
                        recipesRepo.addToFavorites(
                            token, event.recipeID
                        )
                        _state.update {
                            it.copy(
                                isFavorite = !state.value.isFavorite
                            )
                        }

                    }
                }
            }

            is RecipePageEvent.CheckIsFavorite -> {
                viewModelScope.launch {
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    if (token != null) {
                        val own = recipesRepo.isRecipeOwn(token, event.recipeID)
                        _state.update {
                            it.copy(isFavorite = own.data ?: false)
                        }

                    }
                }
            }

            is RecipePageEvent.RemoveFromFavourites -> {
                viewModelScope.launch {
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    if (token != null) {
                        recipesRepo.removeFromFavorites(
                            token, event.recipeID
                        )
                        _state.update {
                            it.copy(
                                isFavorite = !state.value.isFavorite
                            )
                        }

                    }
                }
            }

            RecipePageEvent.ClearData -> {
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
                    )
                }
            }

            is RecipePageEvent.SetFilters -> _state.update {
                it.copy(
                    filters = RecipeResult.Succeed(event.filters)
                )
            }
        }
    }
}


sealed class RecipePageEvent {
    data class LoadRecipe(val receiptId: String) : RecipePageEvent()
    data class SetRecipeName(val receiptName: String) : RecipePageEvent()
    data class SetRecipeDescription(val receiptDescription: String) : RecipePageEvent()
    data class AddIngredient(val ingredient: Ingredient) : RecipePageEvent()
    data class UpdateIngredient(val index: Int, val ingredient: Ingredient) : RecipePageEvent()
    data class RemoveIngredient(val ingredient: Ingredient) : RecipePageEvent()
    data class SetImageUrl(val imageUri: Uri?) : RecipePageEvent()
    data class AddStep(val step: Step) : RecipePageEvent()
    data class RemoveStep(val step: Step) : RecipePageEvent()
    data class UpdateStep(val index: Int, val step: Step) : RecipePageEvent()
    data class AddToFavourites(val recipeID: String) : RecipePageEvent()
    data class RemoveFromFavourites(val recipeID: String) : RecipePageEvent()
    data class CheckIsFavorite(val recipeID: String) : RecipePageEvent()
    data object SaveChanges : RecipePageEvent()
    data object ClearData : RecipePageEvent()
    data class SetFilters(val filters: List<String>) : RecipePageEvent()
}
package com.receipts.receipt_sharing.domain.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.receipts.receipt_sharing.data.recipes.Recipe
import com.receipts.receipt_sharing.data.repositories.AuthDataStoreRepository
import com.receipts.receipt_sharing.data.repositories.FiltersRepositoryImpl
import com.receipts.receipt_sharing.data.repositories.RecipesRepositoryImpl
import com.receipts.receipt_sharing.data.response.RecipeResult
import com.receipts.receipt_sharing.ui.recipe.CellsAmount
import com.receipts.receipt_sharing.ui.recipe.RecipesScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "reipcesScreenVM"

@HiltViewModel
class RecipesScreenViewModel @Inject constructor(
    private val receiptsRepo : RecipesRepositoryImpl,
    private val filtersRepo : FiltersRepositoryImpl
) : ViewModel() {


    private val authDataStoreRepo = AuthDataStoreRepository.get()

    private val _recipes = MutableStateFlow<RecipeResult<List<Recipe>>>(RecipeResult.Downloading())
    private val _state = MutableStateFlow(RecipesScreenState())
    val state = combine(_state, _recipes,){ state, receipts ->
        Log.i(TAG, "vm has changed: $state")
        state.copy(
            recipes = receipts,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), RecipesScreenState())


    init {
        viewModelScope.launch {
            val token = authDataStoreRepo.authDataStoreFlow.first().token
            launch {
                _recipes.update { RecipeResult.Downloading() }
                _recipes.update {
                    token?.let {
                        Log.i(TAG, "start of loading")
                        val data = receiptsRepo.getRecipes(it)
                        data
                    } ?: RecipeResult.Error()
                }
                Log.i(TAG, "End of loading")
            }
            launch {
                if(token != null) {
                    _state.update {
                        it.copy(
                            filters = filtersRepo.getCategorizedFilters(token).data?: emptyMap()
                        )
                    }
                }
            }
        }
    }


    fun onEvent(event : RecipesScreenEvent){
        when(event){
            RecipesScreenEvent.LoadData -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(favoritesLoaded = false,
                            creatorLoaded = false)
                    }
                    _recipes.update { RecipeResult.Downloading() }
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    _recipes.update {
                        token?.let {
                            Log.i(TAG, "start of loading")
                            val data = receiptsRepo.getRecipes(it)
                            data
                        } ?: RecipeResult.Error()
                    }
                    Log.i(TAG, "End of loading")
                }
            }
            RecipesScreenEvent.LoadFavorites -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(favoritesLoaded = true, creatorLoaded = false)
                    }
                    _recipes.update { RecipeResult.Downloading() }
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    _recipes.update {
                        Log.i(TAG, "start of loading favorites")
                        token?.let {
                            receiptsRepo.getFavorites(it)
                        }?:RecipeResult.Error()
                    }
                    Log.i(TAG, "favorites loaded")
                }
            }


            is RecipesScreenEvent.SetSearchName -> viewModelScope.launch {
                val token = authDataStoreRepo.authDataStoreFlow.first().token
                _state.update {
                    it.copy(searchString = event.receiptName)
                }
                _recipes.update { RecipeResult.Downloading() }
                _recipes.update {
                    token?.let {
                        if (event.receiptName.isEmpty()) {
                            when {
                                state.value.favoritesLoaded -> receiptsRepo.getFavorites(it)
                                state.value.creatorLoaded && state.value.selectedCreatorID.isNotEmpty() -> receiptsRepo.getRecipesByCreator(
                                    token,
                                    state.value.selectedCreatorID
                                )
                                else -> receiptsRepo.getRecipes(it)
                            }
                        } else {
                            when {
                                state.value.favoritesLoaded -> receiptsRepo.getFavoritesByName(
                                    it,
                                    event.receiptName
                                )

                                state.value.creatorLoaded && state.value.selectedCreatorID.isNotEmpty() -> receiptsRepo.getRecipesByCreatorByName(
                                    token,
                                    state.value.selectedCreatorID,
                                    event.receiptName
                                )
                                else -> receiptsRepo.getRecipesByName(it, event.receiptName)
                            }
                        }
                    } ?: RecipeResult.Error()
                }
            }

            is RecipesScreenEvent.SetCellsAmount -> _state.update {
                it.copy(cellsCount = event.cellsAmount)
            }

            is RecipesScreenEvent.LoadCreatorsRecipes -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            favoritesLoaded = false,
                            creatorLoaded = true,
                            selectedCreatorID = event.creatorId)
                    }
                    _recipes.update { RecipeResult.Downloading() }
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    _recipes.update {
                        token?.let {
                            receiptsRepo.getRecipesByCreator(it, event.creatorId)
                        }?:RecipeResult.Error()
                    }
                }
            }

            is RecipesScreenEvent.SetFilters -> {
                viewModelScope.launch {
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    _state.update {
                        it.copy(
                            savedFilters = event.filters
                        )
                    }
                    _state.update {
                        it.copy(savedFilters = event.filters)
                    }
                    _recipes.update {
                        RecipeResult.Downloading()
                    }
                    _recipes.update {
                        token?.let {
                            when{
                                state.value.creatorLoaded -> {
                                    if(state.value.searchString.isNotEmpty())
                                        receiptsRepo.getFilteredRecipesByCreatorByName(it, state.value.selectedCreatorID, state.value.searchString, event.filters)
                                    else
                                        receiptsRepo.getFilteredRecipesByCreator(it, state.value.selectedCreatorID, event.filters)
                                }
                                state.value.favoritesLoaded -> {
                                    if(state.value.searchString.isNotEmpty())
                                        receiptsRepo.getFilteredFavorites(it,  event.filters)
                                    else
                                        receiptsRepo.getFilteredFavoritesByName(it, state.value.searchString, event.filters)
                                }
                                else -> {
                                    if(state.value.searchString.isNotEmpty())
                                        receiptsRepo.getFilteredRecipes(it, event.filters)
                                    else
                                        receiptsRepo.getFilteredRecipesByName(it, event.filters, state.value.searchString)
                                }
                            }
                        }?:RecipeResult.Error("Unauthorized")
                    }
                }
            }
            is RecipesScreenEvent.LoadFilters -> viewModelScope.launch {
                val token = authDataStoreRepo.authDataStoreFlow.first().token
                if (!token.isNullOrEmpty())
                    _state.update {
                        it.copy(
                            filters = filtersRepo.getCategorizedFilters(token).data?: emptyMap()
                        )
                    }
            }
        }
    }
}

sealed class RecipesScreenEvent{
    data object LoadData : RecipesScreenEvent()
    data object LoadFavorites : RecipesScreenEvent()
    data class LoadCreatorsRecipes(val creatorId : String) : RecipesScreenEvent()
    data class SetSearchName(val receiptName : String) : RecipesScreenEvent()
    data class SetCellsAmount(val cellsAmount : CellsAmount) : RecipesScreenEvent()
    data class SetFilters(val filters : List<String>) : RecipesScreenEvent()
    data object LoadFilters : RecipesScreenEvent()
}
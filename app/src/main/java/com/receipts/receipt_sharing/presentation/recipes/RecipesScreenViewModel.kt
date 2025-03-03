package com.receipts.receipt_sharing.presentation.recipes

import IRecipesRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.receipts.receipt_sharing.data.repositoriesImpl.AuthDataStoreRepository
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.repositories.IFiltersRepository
import com.receipts.receipt_sharing.domain.response.RecipeResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class RecipesScreenViewModel(
    private val recipesRepo: IRecipesRepository,
    private val filtersRepo: IFiltersRepository
) : ViewModel() {


    private val authDataStoreRepo = AuthDataStoreRepository.get()

    private val _recipes = MutableStateFlow<RecipeResult<List<Recipe>>>(RecipeResult.Downloading())
    private val _state = MutableStateFlow(RecipesScreenState())
    val state = combine(_state, _recipes) { state, receipts ->
        state.copy(
            recipes = receipts,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), RecipesScreenState())


    fun onEvent(event: RecipesScreenEvent) {
        viewModelScope.launch {
            when (event) {
                RecipesScreenEvent.LoadData -> {
                    _state.update {
                        it.copy(
                            favoritesLoaded = false,
                            creatorLoaded = false
                        )
                    }
                    _recipes.update { RecipeResult.Downloading() }
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    _recipes.update {
                        token?.let {
                            val data = recipesRepo.getRecipes(it)
                            data
                        } ?: RecipeResult.Error()
                    }
                }

                RecipesScreenEvent.LoadFavorites -> {
                    _state.update {
                        it.copy(favoritesLoaded = true, creatorLoaded = false)
                    }
                    _recipes.update { RecipeResult.Downloading() }
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    val data = token?.let {
                        recipesRepo.getFavorites(it)
                    } ?: RecipeResult.Error()

                    _recipes.update {
                        data
                    }
                }

                is RecipesScreenEvent.SetSearchName -> {
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    _state.update {
                        it.copy(searchString = event.recipeName)
                    }
                    _recipes.update { RecipeResult.Downloading() }
                    _recipes.update {
                        token?.let {
                            if (event.recipeName.isEmpty()) {
                                when {
                                    state.value.favoritesLoaded -> recipesRepo.getFavorites(it)
                                    state.value.creatorLoaded && state.value.selectedCreatorID.isNotEmpty() -> recipesRepo.getRecipesByCreator(
                                        token,
                                        state.value.selectedCreatorID
                                    )

                                    else -> recipesRepo.getRecipes(it)
                                }
                            } else {
                                when {
                                    state.value.favoritesLoaded -> recipesRepo.getFavoritesByName(
                                        it,
                                        event.recipeName
                                    )

                                    state.value.creatorLoaded && state.value.selectedCreatorID.isNotEmpty() -> recipesRepo.getRecipesByCreatorByName(
                                        token,
                                        state.value.selectedCreatorID,
                                        event.recipeName
                                    )

                                    else -> recipesRepo.getRecipesByName(it, event.recipeName)
                                }
                            }
                        } ?: RecipeResult.Error()
                    }
                }

                is RecipesScreenEvent.SetCellsAmount -> _state.update {
                    it.copy(cellsCount = event.cellsAmount)
                }

                is RecipesScreenEvent.LoadCreatorsRecipes -> {
                    _state.update {
                        it.copy(
                            favoritesLoaded = false,
                            creatorLoaded = true,
                            selectedCreatorID = event.creatorId
                        )
                    }
                    _recipes.update { RecipeResult.Downloading() }
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    _recipes.update {
                        token?.let {
                            recipesRepo.getRecipesByCreator(it, event.creatorId)
                        } ?: RecipeResult.Error()
                    }
                }

                is RecipesScreenEvent.SetFilters -> {
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    _state.update {
                        it.copy(
                            savedFilters = event.filters
                        )
                    }
                    _recipes.update {
                        RecipeResult.Downloading()
                    }
                    _recipes.update {
                        token?.let {
                            when {
                                state.value.creatorLoaded -> {
                                    if (state.value.searchString.isNotEmpty())
                                        recipesRepo.getFilteredRecipesByCreatorByName(
                                            it,
                                            state.value.selectedCreatorID,
                                            state.value.searchString,
                                            event.filters
                                        )
                                    else
                                        recipesRepo.getFilteredRecipesByCreator(
                                            it,
                                            state.value.selectedCreatorID,
                                            event.filters
                                        )
                                }

                                state.value.favoritesLoaded -> {
                                    if (state.value.searchString.isNotEmpty())
                                        recipesRepo.getFilteredFavorites(it, event.filters)
                                    else
                                        recipesRepo.getFilteredFavoritesByName(
                                            it,
                                            state.value.searchString,
                                            event.filters
                                        )
                                }

                                state.value.favoritesLoaded -> {
                                    if (state.value.searchString.isEmpty())
                                        recipesRepo.getFilteredRecipes(it, event.filters)
                                    else
                                        recipesRepo.getFilteredRecipesByName(
                                            it,
                                            event.filters,
                                            state.value.searchString
                                        )
                                }

                                else -> {
                                    RecipeResult.Error()
                                }
                            }
                        } ?: RecipeResult.Error("Unauthorized")
                    }
                }

                is RecipesScreenEvent.LoadFilters -> {
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    if (!token.isNullOrEmpty())
                        _state.update {
                            it.copy(
                                filters = filtersRepo.getCategorizedFilters(token).data
                                    ?: emptyMap()
                            )
                        }
                }

                RecipesScreenEvent.LoadOwnData -> {
                    _state.update {
                        it.copy(
                            favoritesLoaded = true,
                            creatorLoaded = true,
                        )
                    }
                    _recipes.update { RecipeResult.Downloading() }
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    _recipes.update {
                        token?.let {
                            val data = recipesRepo.getOwnRecipes(it)
                            data
                        } ?: RecipeResult.Error()
                    }
                }

                is RecipesScreenEvent.SetOpenSearch -> _state.update {
                    it.copy(openSearch = event.openSearch)
                }

                is RecipesScreenEvent.SetOpenSelectColumnMenu -> _state.update {
                    it.copy(openSelectColumnMenu = event.openMenu)
                }
            }
        }
    }
}

sealed interface RecipesScreenEvent {
    data object LoadData : RecipesScreenEvent
    data object LoadOwnData : RecipesScreenEvent
    data object LoadFavorites : RecipesScreenEvent
    data class LoadCreatorsRecipes(val creatorId: String) : RecipesScreenEvent
    data class SetSearchName(val recipeName: String) : RecipesScreenEvent
    data class SetCellsAmount(val cellsAmount: CellsAmount) : RecipesScreenEvent
    data class SetFilters(val filters: List<String>) : RecipesScreenEvent
    data object LoadFilters : RecipesScreenEvent
    data class SetOpenSelectColumnMenu(val openMenu: Boolean) : RecipesScreenEvent
    data class SetOpenSearch(val openSearch: Boolean) : RecipesScreenEvent
}
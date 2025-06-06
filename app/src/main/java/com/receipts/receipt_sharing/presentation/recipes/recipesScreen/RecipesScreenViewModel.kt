package com.receipts.receipt_sharing.presentation.recipes.recipesScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.receipts.receipt_sharing.data.repositoriesImpl.AuthDataStoreRepository
import com.receipts.receipt_sharing.domain.filters.FiltersModel
import com.receipts.receipt_sharing.domain.filters.OrderingRequest
import com.receipts.receipt_sharing.domain.filters.RecipeFilteringRequest
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.repositories.FiltersRepository
import com.receipts.receipt_sharing.domain.repositories.RecipesRepository
import com.receipts.receipt_sharing.domain.response.ApiResult
import com.receipts.receipt_sharing.presentation.PageSizes
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class RecipesScreenViewModel(
    private val recipesRepo: RecipesRepository,
    private val filtersRepo: FiltersRepository
) : ViewModel() {
    private val authDataStoreRepo = AuthDataStoreRepository.get()
    private var loadingJob: Job? = null
    private val _recipes = MutableStateFlow<ApiResult<List<Recipe>>>(ApiResult.Downloading())
    private val _filters =
        MutableStateFlow<ApiResult<Map<String, List<String>>>>(ApiResult.Downloading())
    private val _state = MutableStateFlow(RecipesScreenState())
    val state = combine(_state, _recipes, _filters) { state, receipts, filters ->
        state.copy(
            recipes = receipts,
            loadedFilters = filters
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), RecipesScreenState())

    /**
     * Processes Recipes screen events
     * @param event Recipes screen event
     * @see [RecipesScreenEvent]
     */
    fun onEvent(event: RecipesScreenEvent) {
        viewModelScope.launch {
            when (event) {
                RecipesScreenEvent.LoadData -> {
                    if (loadingJob?.isActive == true)
                        loadingJob?.cancel()
                    loadingJob = launch {
                        _recipes.update { ApiResult.Downloading() }
                        val token = authDataStoreRepo.authDataStoreFlow.first().token
                        launch {
                            authDataStoreRepo.authDataStoreFlow.first().token?.let { token ->
                                recipesRepo.getTimeStats(token).data?.let { maxTime ->
                                    _state.update {
                                        if (it.timeTo > maxTime)
                                            it.copy(
                                                maxTime = maxTime,
                                                timeTo = maxTime
                                            )
                                        else
                                            it.copy(
                                                maxTime = maxTime,
                                            )
                                    }
                                }
                            }
                        }
                        val result = token?.let { tok ->
                            when (state.value.recipesLoadedDataType) {
                                RecipesLoadedDataType.OwnDataRecipes -> {
                                    if (state.value.searchString.isEmpty()) recipesRepo.getFilteredOwnRecipes(
                                        tok,
                                        RecipeFilteringRequest(
                                            ordering = state.value.recipeOrdering?.let {
                                                OrderingRequest(
                                                    it,
                                                    state.value.ascending
                                                )
                                            },
                                            filters = FiltersModel(
                                                tags = state.value.searchedFilters,
                                                ingredients = state.value.searchedIngredients
                                            )
                                        ),
                                        state.value.currentPage,
                                        state.value.pageSize.pageSize,
                                    ) else recipesRepo.getFilteredOwnRecipesByName(
                                        tok,
                                        state.value.searchString,
                                        RecipeFilteringRequest(
                                            ordering = state.value.recipeOrdering?.let {
                                                OrderingRequest(
                                                    it,
                                                    state.value.ascending
                                                )
                                            },
                                            filters = FiltersModel(
                                                tags = state.value.searchedFilters,
                                                ingredients = state.value.searchedIngredients
                                            )
                                        ),
                                        state.value.currentPage,
                                        state.value.pageSize.pageSize,
                                    )
                                }

                                is RecipesLoadedDataType.CreatorRecipes -> {
                                    if (state.value.searchString.isEmpty()) recipesRepo.getFilteredRecipesByCreator(
                                        tok,
                                        (state.value.recipesLoadedDataType as RecipesLoadedDataType.CreatorRecipes).creatorID,
                                        RecipeFilteringRequest(
                                            ordering = state.value.recipeOrdering?.let {
                                                OrderingRequest(
                                                    it,
                                                    state.value.ascending
                                                )
                                            },
                                            filters = FiltersModel(
                                                tags = state.value.searchedFilters,
                                                ingredients = state.value.searchedIngredients
                                            )
                                        ),
                                        state.value.currentPage,
                                        state.value.pageSize.pageSize,
                                    ) else recipesRepo.getFilteredRecipesByCreatorByName(
                                        tok,
                                        (state.value.recipesLoadedDataType as RecipesLoadedDataType.CreatorRecipes).creatorID,
                                        state.value.searchString,
                                        RecipeFilteringRequest(
                                            ordering = state.value.recipeOrdering?.let {
                                                OrderingRequest(
                                                    it,
                                                    state.value.ascending
                                                )
                                            },
                                            filters = FiltersModel(
                                                tags = state.value.searchedFilters,
                                                ingredients = state.value.searchedIngredients
                                            )
                                        ),
                                        state.value.currentPage,
                                        state.value.pageSize.pageSize,
                                    )
                                }

                                RecipesLoadedDataType.Favorites -> {
                                    if (state.value.searchString.isEmpty()) recipesRepo.getFilteredFavorites(
                                        tok,
                                        RecipeFilteringRequest(
                                            ordering = state.value.recipeOrdering?.let {
                                                OrderingRequest(
                                                    it,
                                                    state.value.ascending
                                                )
                                            },
                                            filters = FiltersModel(
                                                tags = state.value.searchedFilters,
                                                ingredients = state.value.searchedIngredients
                                            )
                                        ),
                                        state.value.currentPage,
                                        state.value.pageSize.pageSize,
                                    ) else recipesRepo.getFilteredFavoritesByName(
                                        tok,
                                        state.value.searchString,
                                        RecipeFilteringRequest(
                                            ordering = state.value.recipeOrdering?.let {
                                                OrderingRequest(
                                                    it,
                                                    state.value.ascending
                                                )
                                            },
                                            filters = FiltersModel(
                                                tags = state.value.searchedFilters,
                                                ingredients = state.value.searchedIngredients
                                            )
                                        ),
                                        state.value.currentPage,
                                        state.value.pageSize.pageSize,
                                    )
                                }

                                RecipesLoadedDataType.All -> {
                                    if (state.value.searchString.isEmpty()) recipesRepo.getFilteredRecipes(
                                        tok,
                                        RecipeFilteringRequest(
                                            ordering = state.value.recipeOrdering?.let {
                                                OrderingRequest(
                                                    it,
                                                    state.value.ascending
                                                )
                                            },
                                            filters = FiltersModel(
                                                tags = state.value.searchedFilters,
                                                ingredients = state.value.searchedIngredients
                                            )
                                        ),
                                        state.value.currentPage,
                                        state.value.pageSize.pageSize,
                                    ) else recipesRepo.getFilteredRecipesByName(
                                        tok,
                                        RecipeFilteringRequest(
                                            ordering = state.value.recipeOrdering?.let {
                                                OrderingRequest(
                                                    it,
                                                    state.value.ascending
                                                )
                                            },
                                            filters = FiltersModel(
                                                tags = state.value.searchedFilters,
                                                ingredients = state.value.searchedIngredients
                                            )
                                        ),
                                        state.value.searchString,
                                        state.value.currentPage,
                                        state.value.pageSize.pageSize,
                                    )
                                }

                                is RecipesLoadedDataType.Similar -> {
                                    if (state.value.searchString.isEmpty()) recipesRepo.getFilteredRecipes(
                                        tok,
                                        RecipeFilteringRequest(
                                            ordering = state.value.recipeOrdering?.let {
                                                OrderingRequest(
                                                    it,
                                                    state.value.ascending
                                                )
                                            },
                                            filters = FiltersModel(
                                                tags = state.value.searchedFilters,
                                                ingredients = state.value.searchedIngredients
                                            )
                                        ),
                                        state.value.currentPage,
                                        state.value.pageSize.pageSize,
                                    ) else recipesRepo.getFilteredRecipesByName(
                                        tok,
                                        RecipeFilteringRequest(
                                            ordering = state.value.recipeOrdering?.let {
                                                OrderingRequest(
                                                    it,
                                                    state.value.ascending
                                                )
                                            },
                                            filters = FiltersModel(
                                                tags = state.value.searchedFilters,
                                                ingredients = state.value.searchedIngredients
                                            )
                                        ),
                                        state.value.searchString,
                                        state.value.currentPage,
                                        state.value.pageSize.pageSize,
                                    )
                                }
                            }
                        } ?: ApiResult.Error()
                        when (result) {
                            is ApiResult.Downloading -> {}
                            is ApiResult.Error -> _recipes.update {
                                ApiResult.Error(result.info ?: "Unknown error")
                            }

                            is ApiResult.Succeed -> {
                                _state.update {
                                    it.copy(
                                        currentPage = result.data?.currentPage ?: it.currentPage,
                                        maxPages = result.data?.totalPages ?: it.currentPage
                                    )
                                }
                                _recipes.update {
                                    ApiResult.Succeed(result.data?.result)
                                }
                            }
                        }
                    }
                }

                is RecipesScreenEvent.SetSearchName -> {
                    _state.update {
                        it.copy(searchString = event.recipeName)
                    }
                    onEvent(RecipesScreenEvent.LoadData)
                }

                is RecipesScreenEvent.SetCellsAmount -> _state.update {
                    it.copy(cellsCount = event.cellsAmount)
                }

                is RecipesScreenEvent.SetFilters -> {
                    _state.update {
                        it.copy(
                            searchedFilters = event.filters
                        )
                    }
                }

                is RecipesScreenEvent.SetIngredients -> {
                    _state.update {
                        it.copy(
                            searchedIngredients = event.ingredients
                        )
                    }
                }

                is RecipesScreenEvent.SetOrdering -> {
                    _state.update {
                        if (it.recipeOrdering == event.ordering)
                            it.copy(ascending = !it.ascending)
                        else
                            it.copy(
                                recipeOrdering = event.ordering,
                                ascending = true
                            )
                    }
                    onEvent(RecipesScreenEvent.LoadData)
                }

                is RecipesScreenEvent.LoadFilters -> {
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    if (!token.isNullOrEmpty())
                        _filters.update {
                            filtersRepo.getCategorizedFilters(token)
                        }
                }

                is RecipesScreenEvent.SetOpenSearch -> _state.update {
                    it.copy(openSearch = event.openSearch)
                }

                is RecipesScreenEvent.SetOpenSelectColumnMenu -> _state.update {
                    it.copy(openSelectColumnMenu = event.openMenu)
                }

                is RecipesScreenEvent.SetOpenSelectOrderingMenu -> _state.update {
                    it.copy(openSelectOrderingMenu = event.openMenu)
                }

                is RecipesScreenEvent.SetOpenFiltersPage -> {
                    _state.update {
                        it.copy(openFiltersPage = event.openDialog)
                    }
                    if (event.openDialog) {
                        onEvent(RecipesScreenEvent.LoadFilters)
                    }
                }

                is RecipesScreenEvent.SetCurrentPage -> {
                    _state.update {
                        it.copy(currentPage = event.currentPage)
                    }
                    onEvent(RecipesScreenEvent.LoadData)
                }

                is RecipesScreenEvent.SetPageSize -> {
                    _state.update {
                        it.copy(pageSize = event.pageSizes)
                    }
                    onEvent(RecipesScreenEvent.LoadData)
                }

                is RecipesScreenEvent.SetLoadDataType -> {
                    when {
                        state.value.recipesLoadedDataType != event.dataType -> {
                            _state.update {
                                it.copy(
                                    recipesLoadedDataType = event.dataType,
                                    searchedFilters = emptyList(),
                                    searchedIngredients = emptyList(),
                                    searchString = "",
                                    pageSize = PageSizes.Standard,
                                    currentPage = 1
                                )
                            }
                            onEvent(RecipesScreenEvent.LoadData)
                        }

                        state.value.recipes !is ApiResult.Succeed -> {
                            onEvent(RecipesScreenEvent.LoadData)
                        }
                    }
                }

                is RecipesScreenEvent.SetExpandFiltersTab -> {
                    _state.update {
                        it.copy(expandFiltersTab = event.expandTab)
                    }
                }

                RecipesScreenEvent.ClearFilters -> {
                    _state.update {
                        it.copy(
                            timeFrom = state.value.minTime,
                            timeTo = state.value.maxTime,
                            searchedIngredients = emptyList(),
                            searchedFilters = emptyList()
                        )
                    }
                    onEvent(RecipesScreenEvent.LoadData)
                }

                is RecipesScreenEvent.SetTimeFrom -> _state.update {
                    it.copy(
                        timeFrom = event.timeFrom
                    )
                }

                is RecipesScreenEvent.SetTimeTo -> _state.update {
                    it.copy(
                        timeTo = event.timeTo
                    )
                }
            }
        }
    }
}
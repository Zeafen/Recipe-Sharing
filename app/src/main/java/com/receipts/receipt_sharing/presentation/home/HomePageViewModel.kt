package com.receipts.receipt_sharing.presentation.home

import RecipesRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.receipts.receipt_sharing.data.repositoriesImpl.AuthDataStoreRepository
import com.receipts.receipt_sharing.domain.creators.CreatorRequest
import com.receipts.receipt_sharing.domain.filters.OrderingRequest
import com.receipts.receipt_sharing.domain.filters.RecipeFilteringRequest
import com.receipts.receipt_sharing.domain.filters.RecipeOrdering
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.repositories.CreatorsRepository
import com.receipts.receipt_sharing.domain.response.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomePageViewModel(
    private val recipesRepo: RecipesRepository,
    private val creatorsRepo: CreatorsRepository
) : ViewModel() {
    private val authDataStoreRepo = AuthDataStoreRepository.get()

    private val _publishers: MutableStateFlow<ApiResult<List<CreatorRequest>>> =
        MutableStateFlow(ApiResult.Downloading())
    private val _popularRecipes: MutableStateFlow<ApiResult<List<Recipe>>> =
        MutableStateFlow(ApiResult.Downloading())
    private val _recentRecipes: MutableStateFlow<ApiResult<List<Recipe>>> =
        MutableStateFlow(ApiResult.Downloading())

    private val _state: MutableStateFlow<HomePageState> = MutableStateFlow(HomePageState())
    val state : StateFlow<HomePageState> = combine(
        _publishers,
        _recentRecipes,
        _popularRecipes,
        _state
    ) { publishers, recents, populars, state ->
        state.copy(
            topPublishers = publishers,
            recentRecipes = recents,
            popularRecipes = populars
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), _state.value)

    init {
        viewModelScope.launch {
            authDataStoreRepo.authDataStoreFlow.collect { userInfo ->
                _state.update { state ->
                    state.copy(
                        userName = userInfo.userName,
                    )
                }
            }
        }
    }

    /**
     * Processes Home page events
     * @param event Home page event
     * @see [HomePageEvent]
     */
    fun onEvent(event : HomePageEvent){
        viewModelScope.launch {
            when(event){
                HomePageEvent.LoadData -> {
                    launch {
                        _popularRecipes.update {
                            ApiResult.Downloading()
                        }
                        val token = authDataStoreRepo.authDataStoreFlow.first().token
                        val result = token?.let { tok ->
                            recipesRepo.getFilteredRecipes(
                                tok, RecipeFilteringRequest(
                                    emptyList(),
                                    OrderingRequest(RecipeOrdering.Rating, false)
                                ),
                                1,
                                15
                            )
                        } ?: ApiResult.Error("Unauthorized")
                        _popularRecipes.update {
                            when (result) {
                                is ApiResult.Downloading -> ApiResult.Downloading()
                                is ApiResult.Error -> ApiResult.Error(result.info)
                                is ApiResult.Succeed -> ApiResult.Succeed(result.data?.result)
                            }
                        }
                    }
                    launch {
                        _recentRecipes.update {
                            ApiResult.Downloading()
                        }
                        val token = authDataStoreRepo.authDataStoreFlow.first().token
                        val result = token?.let { tok ->
                            recipesRepo.getFilteredRecipes(
                                tok, RecipeFilteringRequest(
                                    emptyList(),
                                    OrderingRequest(RecipeOrdering.DatePublished, false)
                                ),
                                1,
                                30
                            )
                        } ?: ApiResult.Error("Unauthorized")
                        _recentRecipes.update {
                            when (result) {
                                is ApiResult.Downloading -> ApiResult.Downloading()
                                is ApiResult.Error -> ApiResult.Error(result.info)
                                is ApiResult.Succeed -> ApiResult.Succeed(result.data?.result)
                            }
                        }
                    }
                    launch {
                        _publishers.update {
                            ApiResult.Downloading()
                        }
                        val token = authDataStoreRepo.authDataStoreFlow.first().token
                        val result = token?.let { tok ->
                            creatorsRepo.getTopCreators(tok, 1, 15)
                        } ?: ApiResult.Error("Unauthorized")
                        _publishers.update {
                            when (result) {
                                is ApiResult.Downloading -> ApiResult.Downloading()
                                is ApiResult.Error -> ApiResult.Error(result.info)
                                is ApiResult.Succeed -> ApiResult.Succeed(result.data?.result)
                            }
                        }
                    }
                }
                HomePageEvent.LoadPopulars -> {
                    _popularRecipes.update {
                        ApiResult.Downloading()
                    }
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    val result = token?.let { tok ->
                        recipesRepo.getFilteredRecipes(
                            tok, RecipeFilteringRequest(
                                emptyList(),
                                OrderingRequest(RecipeOrdering.Rating, false)
                            ),
                            1,
                            15
                        )
                    } ?: ApiResult.Error("Unauthorized")
                    _popularRecipes.update {
                        when (result) {
                            is ApiResult.Downloading -> ApiResult.Downloading()
                            is ApiResult.Error -> ApiResult.Error(result.info)
                            is ApiResult.Succeed -> ApiResult.Succeed(result.data?.result)
                        }
                    }
                }
                HomePageEvent.LoadPublishers -> {
                    _publishers.update {
                        ApiResult.Downloading()
                    }
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    val result = token?.let { tok ->
                        creatorsRepo.getTopCreators(tok, 1, 15)
                    } ?: ApiResult.Error("Unauthorized")
                    _publishers.update {
                        when (result) {
                            is ApiResult.Downloading -> ApiResult.Downloading()
                            is ApiResult.Error -> ApiResult.Error(result.info)
                            is ApiResult.Succeed -> ApiResult.Succeed(result.data?.result)
                        }
                    }
                }
                HomePageEvent.LoadRecents -> {
                    _recentRecipes.update {
                        ApiResult.Downloading()
                    }
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    val result = token?.let { tok ->
                        recipesRepo.getFilteredRecipes(
                            tok, RecipeFilteringRequest(
                                emptyList(),
                                OrderingRequest(RecipeOrdering.DatePublished, false)
                            ),
                            1,
                            30
                        )
                    } ?: ApiResult.Error("Unauthorized")
                    _recentRecipes.update {
                        when (result) {
                            is ApiResult.Downloading -> ApiResult.Downloading()
                            is ApiResult.Error -> ApiResult.Error(result.info)
                            is ApiResult.Succeed -> ApiResult.Succeed(result.data?.result)
                        }
                    }
                }
            }
        }
    }
}
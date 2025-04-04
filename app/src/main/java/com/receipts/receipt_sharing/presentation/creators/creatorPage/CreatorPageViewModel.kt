package com.receipts.receipt_sharing.presentation.creators.creatorPage

import RecipesRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.receipts.receipt_sharing.data.repositoriesImpl.AuthDataStoreRepository
import com.receipts.receipt_sharing.domain.creators.CreatorRequest
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.repositories.CreatorsRepository
import com.receipts.receipt_sharing.domain.response.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreatorPageViewModel(
    private val recipesRepo: RecipesRepository,
    private val creatorRepo: CreatorsRepository,
) : ViewModel() {

    private val authDataStoreRepo = AuthDataStoreRepository.get()

    private val _creator =
        MutableStateFlow<ApiResult<CreatorRequest>>(ApiResult.Downloading())
    private val _recipes = MutableStateFlow<ApiResult<List<Recipe>>>(ApiResult.Downloading())
    private val _state = MutableStateFlow(CreatorPageState())

    val state = combine(_creator, _recipes, _state) { creator, recipes, state ->
        state.copy(creator = creator, recipes = recipes)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), CreatorPageState())

    /**
     * Processes Creator info page events
     * @param event Creator info page event
     * @see [CreatorPageEvent]
     */
    fun onEvent(event: CreatorPageEvent) {
        viewModelScope.launch {
            when (event) {
                is CreatorPageEvent.LoadCreator -> {
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    launch {
                        _creator.update {
                            ApiResult.Downloading()
                        }
                        _creator.update {
                            token?.let {
                                creatorRepo.getCreatorById(it, event.creatorID)
                            } ?: ApiResult.Error()
                        }

                        if (_creator.value is ApiResult.Succeed)
                            _state.update {
                                it.copy(
                                    follows = token?.let {
                                        creatorRepo.doesFollow(token, event.creatorID).data
                                            ?: false
                                    } ?: false,
                                    followersCount = _creator.value.data?.followersCount ?: 0,
                                    followsCount = token?.let {
                                        creatorRepo.getCreatorFollowsCount(
                                            it,
                                            _creator.value.data!!.userID
                                        ).data ?: 0
                                    } ?: 0,
                                    expandAboutMe = true
                                )
                            }
                    }

                    launch {
                        _recipes.update {
                            ApiResult.Downloading()
                        }
                        _recipes.update {
                            authDataStoreRepo.authDataStoreFlow.first().token?.let {
                                recipesRepo.getTopRecipesByCreator(it, event.creatorID)
                            } ?: ApiResult.Error()
                        }
                    }
                }

                CreatorPageEvent.ChangeFollows -> {
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    val result = token?.let { tok ->
                        state.value.creator.data?.let {
                            when (state.value.follows) {
                                true -> creatorRepo.removeFromFollows(tok, it.userID)
                                false -> creatorRepo.addToFollows(tok, it.userID)
                            }
                        }
                    }
                    val follows = when (result) {
                        is ApiResult.Succeed -> !state.value.follows
                        else -> state.value.follows
                    }
                    val followersCount = token?.let {
                        creatorRepo.getCreatorFollowersCount(
                            it,
                            state.value.creator.data!!.userID
                        ).data
                    } ?: 0
                    _state.update {
                        it.copy(
                            follows = follows,
                            followersCount = followersCount
                        )
                    }
                }

                CreatorPageEvent.ReloadRecipes -> {
                    _recipes.update {
                        ApiResult.Downloading()
                    }
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    _recipes.update {
                        token?.let { tok ->
                            state.value.creator.data?.let { creator ->
                                recipesRepo.getTopRecipesByCreator(tok, creator.userID)
                            } ?: ApiResult.Error("Creator haven't been loaded")
                        } ?: ApiResult.Error("Cannot find token")
                    }
                }

                is CreatorPageEvent.SetExpandAboutMe -> _state.update {
                    it.copy(expandAboutMe = event.expand)
                }
            }
        }
    }
}
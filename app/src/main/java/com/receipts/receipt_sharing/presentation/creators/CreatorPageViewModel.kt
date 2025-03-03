package com.receipts.receipt_sharing.presentation.creators

import IRecipesRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.receipts.receipt_sharing.data.repositoriesImpl.AuthDataStoreRepository
import com.receipts.receipt_sharing.domain.creators.CreatorRequest
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.repositories.ICreatorsRepository
import com.receipts.receipt_sharing.domain.response.RecipeResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreatorPageViewModel(
    private val recipesRepo: IRecipesRepository,
    private val creatorRepo: ICreatorsRepository,
) : ViewModel() {

    private val authDataStoreRepo = AuthDataStoreRepository.get()

    private val _creator =
        MutableStateFlow<RecipeResult<CreatorRequest>>(RecipeResult.Downloading())
    private val _recipes = MutableStateFlow<RecipeResult<List<Recipe>>>(RecipeResult.Downloading())
    private val _state = MutableStateFlow(CreatorPageState())

    val state = combine(_creator, _recipes, _state) { creator, recipes, state ->
        state.copy(creator = creator, recipes = recipes)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), CreatorPageState())

    fun onEvent(event: CreatorPageEvent) {
        viewModelScope.launch {
            when (event) {

                is CreatorPageEvent.LoadCreator -> {
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    launch {
                        _creator.update {
                            RecipeResult.Downloading()
                        }
                        _creator.update {
                            token?.let {
                                creatorRepo.getCreatorById(it, event.creatorID)
                            } ?: RecipeResult.Error()
                        }

                        if (_creator.value is RecipeResult.Succeed)
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
                                    } ?: 0
                                )
                            }
                    }

                    launch {
                        _recipes.update {
                            RecipeResult.Downloading()
                        }
                        _recipes.update {
                            authDataStoreRepo.authDataStoreFlow.first().token?.let {
                                recipesRepo.getRecipesByCreator(it, event.creatorID)
                            } ?: RecipeResult.Error()
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

                    launch {
                        val follows = when (result) {
                            is RecipeResult.Succeed -> !state.value.follows
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
                }

                CreatorPageEvent.ReloadRecipes -> {
                    _recipes.update {
                        RecipeResult.Downloading()
                    }
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    _recipes.update {
                        token?.let { tok ->
                            state.value.creator.data?.let { creator ->
                                recipesRepo.getRecipesByCreator(tok, creator.userID)
                            } ?: RecipeResult.Error("Creator haven't been loaded")
                        } ?: RecipeResult.Error("Cannot find token")
                    }
                }

            }
        }
    }
}


sealed interface CreatorPageEvent {
    data class LoadCreator(val creatorID: String) : CreatorPageEvent
    data object ChangeFollows : CreatorPageEvent
    data object ReloadRecipes : CreatorPageEvent
}
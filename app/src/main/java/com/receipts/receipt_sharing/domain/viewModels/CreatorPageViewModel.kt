package com.receipts.receipt_sharing.domain.viewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.receipts.receipt_sharing.data.CreatorRequest
import com.receipts.receipt_sharing.data.recipes.Recipe
import com.receipts.receipt_sharing.data.repositories.AuthDataStoreRepository
import com.receipts.receipt_sharing.data.repositories.CreatorsRepositoryImpl
import com.receipts.receipt_sharing.data.repositories.RecipesRepositoryImpl
import com.receipts.receipt_sharing.data.response.RecipeResult
import com.receipts.receipt_sharing.domain.helpers.FileHelper
import com.receipts.receipt_sharing.ui.creators.CreatorPageState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class CreatorPageViewModel @Inject constructor(
    private val recipesRepo : RecipesRepositoryImpl,
    private val creatorRepo : CreatorsRepositoryImpl,
) : ViewModel() {

    private val authDataStore = AuthDataStoreRepository.get()

    private val _creator = MutableStateFlow<RecipeResult<CreatorRequest>>(RecipeResult.Downloading())
    private val _recipes = MutableStateFlow<RecipeResult<List<Recipe>>>(RecipeResult.Downloading())
    private val _state = MutableStateFlow(CreatorPageState())

    val state = combine(_creator, _recipes, _state){ creator, recipes, state ->
        state.copy(creator = creator, recipes = recipes)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), CreatorPageState())

    fun onEvent(event : CreatorPageEvent){
        when(event){
            CreatorPageEvent.AddToFollows -> {
                viewModelScope.launch {
                    val token = authDataStore.authDataStoreFlow.first().token
                    val follows = token?.let {
                        if(state.value.creator.data != null){
                            when(creatorRepo.addToFollows(it, state.value.creator.data!!.userID)){
                                is RecipeResult.Succeed -> !state.value.follows
                                else -> false
                            }
                        }
                        else false
                    }?:false
                    val followersCount = token?.let {
                        creatorRepo.getCreatorFollowers(it, state.value.creator.data!!.userID).data?.size
                    }?:0
                    _state.update {
                        it.copy(
                            follows = follows,
                            followers = followersCount
                        )
                    }
                }
            }
            is CreatorPageEvent.LoadCreator -> {
                viewModelScope.launch {
                    val token = authDataStore.authDataStoreFlow.first().token
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
                                    creatorName = _creator.value.data?.nickname ?: "",
                                    imageUrl = _creator.value.data?.imageUrl,
                                    follows = token?.let {
                                        creatorRepo.doesFollow(token, event.creatorID).data ?: false
                                    } ?: false,
                                    followers = token?.let {
                                        creatorRepo.getCreatorFollowers(
                                            it,
                                            event.creatorID
                                        ).data?.size ?: 0
                                    } ?: 0
                                )
                            }
                    }
                    launch {
                        _recipes.update {
                            RecipeResult.Downloading()
                        }
                        _recipes.update {
                            authDataStore.authDataStoreFlow.first().token?.let {
                                recipesRepo.getRecipesByCreator(it, event.creatorID)
                            } ?: RecipeResult.Error()
                        }
                    }
                }
            }
            CreatorPageEvent.RemoveFromFollows -> {
                viewModelScope.launch {
                    val token = authDataStore.authDataStoreFlow.first().token
                    val follows = token?.let {
                        if(state.value.creator.data != null){
                            when(creatorRepo.removeFromFollows(it, state.value.creator.data!!.userID)){
                                is RecipeResult.Succeed -> !state.value.follows
                                else -> false
                            }
                        }
                        else false
                    }?:false
                    val followersCount = token?.let {
                        creatorRepo.getCreatorFollowers(it, state.value.creator.data!!.userID).data?.size
                    }?:0
                    _state.update {
                        it.copy(
                            follows = follows,
                            followers = followersCount
                        )
                    }
                }
            }
            is CreatorPageEvent.SetCreatorName -> _state.update {
                it.copy(creatorName = event.name)
            }
            is CreatorPageEvent.SetImageUrl -> {
                viewModelScope.launch {

                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token != null) {
                        val file = FileHelper.get().getFileFromUri(event.imageUri)
                        file?.let { imageUri ->
                            val url = recipesRepo.uploadCreatorImage(
                                token,
                                file
                            )

                            url.data?.let { resPath ->
                                _state.update {
                                    it.copy(
                                        imageUrl = resPath
                                    )
                                }
                            }
                        }
                    }
                }
            }

            CreatorPageEvent.SaveChanges -> {
                viewModelScope.launch {
                    val creatorName = state.value.creatorName
                    val imageUrl = state.value.imageUrl
                    if (creatorName.isBlank())
                        return@launch
                    val creator = state.value.creator.data?.copy(
                        nickname = creatorName,
                        imageUrl = imageUrl ?: state.value.creator.data!!.imageUrl
                    )
                    authDataStore.authDataStoreFlow.first().token?.let {
                        if(creator != null)
                            creatorRepo.updateCreator(it, creator)
                    }
                }
            }

            CreatorPageEvent.LoadUserInfo -> {
                viewModelScope.launch {
                    val token = authDataStore.authDataStoreFlow.first().token
                    _creator.update {
                        RecipeResult.Downloading()
                    }

                    runBlocking {
                        _creator.update {
                            token?.let {
                                creatorRepo.getUserInfo(it)
                            } ?: RecipeResult.Error()
                        }

                        if (_creator.value is RecipeResult.Succeed)
                            _state.update {
                                it.copy(
                                    creatorName = _creator.value.data?.nickname ?: "",
                                    imageUrl = _creator.value.data?.imageUrl,
                                    follows = false,
                                    followers = token?.let {
                                        creatorRepo.getFollowers(it).data?.size ?: 0
                                    } ?: 0
                                )
                            }
                    }

                    launch {
                        _recipes.update {
                            RecipeResult.Downloading()
                        }
                        _recipes.update {
                            authDataStore.authDataStoreFlow.first().token?.let {
                                recipesRepo.getRecipes(it)
                            } ?: RecipeResult.Error()
                        }
                    }
                }
            }

            CreatorPageEvent.ReloadRecipes -> {
                viewModelScope.launch {
                    _recipes.update {
                        RecipeResult.Downloading()
                    }
                    val token = authDataStore.authDataStoreFlow.first().token
                    _recipes.update {
                        token?.let {tok ->
                            state.value.creator.data?.let {creator ->
                            recipesRepo.getRecipesByCreator(tok, creator.userID)
                            } ?: RecipeResult.Error("Creator haven't been loaded")
                        } ?: RecipeResult.Error("Cannot find token")
                    }
                }
            }
        }
    }
}

sealed class CreatorPageEvent{
    data class LoadCreator(val creatorID : String) : CreatorPageEvent()
    data object LoadUserInfo : CreatorPageEvent()
    data class SetCreatorName(val name : String) : CreatorPageEvent()
    data class SetImageUrl(val imageUri : Uri?) : CreatorPageEvent()
    data object AddToFollows : CreatorPageEvent()
    data object RemoveFromFollows : CreatorPageEvent()
    data object SaveChanges : CreatorPageEvent()
    data object ReloadRecipes : CreatorPageEvent()
}
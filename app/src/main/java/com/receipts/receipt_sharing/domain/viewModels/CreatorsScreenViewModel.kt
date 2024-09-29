package com.receipts.receipt_sharing.domain.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.receipts.receipt_sharing.data.CreatorRequest
import com.receipts.receipt_sharing.data.repositories.AuthDataStoreRepository
import com.receipts.receipt_sharing.data.repositories.CreatorsRepositoryImpl
import com.receipts.receipt_sharing.data.response.RecipeResult
import com.receipts.receipt_sharing.ui.creators.CreatorsScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatorsScreenViewModel @Inject constructor(
    private val creatorsRepo : CreatorsRepositoryImpl,
): ViewModel() {

    private val authDataStore = AuthDataStoreRepository.get()

    private val _creators =
        MutableStateFlow<RecipeResult<List<CreatorRequest>>>(RecipeResult.Downloading())

    private val _state = MutableStateFlow(CreatorsScreenState())

    val state = combine(_creators, _state) { creators, state ->
        state.copy(creators = creators)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(300), CreatorsScreenState())


    init {
        onEvent(CreatorsScreenEvent.LoadData)
    }

    fun onEvent(event: CreatorsScreenEvent) {
        when (event) {
            CreatorsScreenEvent.LoadData -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(followsLoaded = false)
                    }
                    val token = authDataStore.authDataStoreFlow.first().token
                    _creators.update {
                        RecipeResult.Downloading()
                    }
                    _creators.update {
                        token?.let { creatorsRepo.getCreators(token) } ?: RecipeResult.Error()
                    }
                }
            }

            CreatorsScreenEvent.LoadFollows -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(followsLoaded = true)
                    }
                    val token = authDataStore.authDataStoreFlow.first().token
                    _creators.update {
                        RecipeResult.Downloading()
                    }
                    _creators.update {
                        token?.let {
                            creatorsRepo.getFollows(token)
                        } ?: RecipeResult.Error()
                    }
                    Log.i("follows_loading", _creators.value.data?.toString()?:"nothing here")
                }
            }

            is CreatorsScreenEvent.SetSearchName -> {
                viewModelScope.launch {
                    val token = authDataStore.authDataStoreFlow.first().token
                    _state.update {
                        it.copy(searchedName = event.searchString)
                    }
                    _creators.update {
                        RecipeResult.Downloading()
                    }
                    _creators.update {
                        token?.let {
                            if (event.searchString.isEmpty()){
                                if(state.value.followsLoaded)
                                    creatorsRepo.getFollows(it)
                                else
                                    creatorsRepo.getCreators(token)
                            }
                            else{
                                if(state.value.followsLoaded)
                                    creatorsRepo.getFollowsByName(it, event.searchString)
                                else
                                    creatorsRepo.getCreatorsByName(token, event.searchString)
                            }
                        } ?: RecipeResult.Error()
                    }
                }
            }

            is CreatorsScreenEvent.LoadFollowers -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(followsLoaded = true)
                    }
                    viewModelScope.launch {
                        val token = authDataStore.authDataStoreFlow.first().token
                        _creators.update {
                            RecipeResult.Downloading()
                        }
                        _creators.update {
                            token?.let {
                                if(event.creatorID.isNullOrEmpty())
                                    creatorsRepo.getFollowers(it)
                                else creatorsRepo.getCreatorFollowers(it, event.creatorID)
                            } ?: RecipeResult.Error()
                        }
                    }
                }
            }
        }
    }
}

sealed class CreatorsScreenEvent{
    data object LoadData : CreatorsScreenEvent()
    data object LoadFollows : CreatorsScreenEvent()
    data class LoadFollowers(val creatorID : String? = null) : CreatorsScreenEvent()
    data class SetSearchName(val searchString : String) : CreatorsScreenEvent()
}
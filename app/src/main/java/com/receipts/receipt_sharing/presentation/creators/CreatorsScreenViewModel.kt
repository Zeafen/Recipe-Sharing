package com.receipts.receipt_sharing.presentation.creators

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.receipts.receipt_sharing.data.repositoriesImpl.AuthDataStoreRepository
import com.receipts.receipt_sharing.domain.creators.CreatorRequest
import com.receipts.receipt_sharing.domain.repositories.ICreatorsRepository
import com.receipts.receipt_sharing.domain.response.RecipeResult
import com.receipts.receipt_sharing.presentation.recipes.CellsAmount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreatorsScreenViewModel(
    private val creatorsRepo: ICreatorsRepository,
) : ViewModel() {

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
        viewModelScope.launch {
            when (event) {
                CreatorsScreenEvent.LoadData -> {
                    _state.update {
                        it.copy(followsLoaded = false)
                    }
                    val token = authDataStore.authDataStoreFlow.first().token
                    _creators.update {
                        RecipeResult.Downloading()
                    }
                    _creators.update {
                        token?.let {
                            creatorsRepo.getCreators(it)
                        }?:RecipeResult.Error("Unauthorized")
                    }
                }

                CreatorsScreenEvent.LoadFollows -> {
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
                    Log.i("follows_loading", _creators.value.data?.toString() ?: "nothing here")
                }

                is CreatorsScreenEvent.SetSearchName -> {
                    val token = authDataStore.authDataStoreFlow.first().token
                    _state.update {
                        it.copy(searchedName = event.searchString)
                    }
                    _creators.update {
                        RecipeResult.Downloading()
                    }
                    _creators.update {
                        token?.let {
                            if (event.searchString.isEmpty()) {
                                if (state.value.followsLoaded)
                                    creatorsRepo.getFollows(it)
                                else
                                    creatorsRepo.getCreators(token)
                            } else {
                                if (state.value.followsLoaded)
                                    creatorsRepo.getFollowsByName(it, event.searchString)
                                else
                                    creatorsRepo.getCreatorsByName(token, event.searchString)
                            }
                        } ?: RecipeResult.Error()
                    }
                }

                is CreatorsScreenEvent.LoadFollowers -> {
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
                                if (event.creatorID.isNullOrEmpty())
                                    creatorsRepo.getFollowers(it)
                                else creatorsRepo.getCreatorFollowers(it, event.creatorID)
                            } ?: RecipeResult.Error()
                        }
                    }
                }
                is CreatorsScreenEvent.SetOpenSearchString -> _state.update {
                    it.copy(openSearchString = event.openSearchString)
                }

                is CreatorsScreenEvent.SetCellsAmount -> _state.update {
                    it.copy(cellsAmount = event.cellsAmount)
                }
                is CreatorsScreenEvent.SetOpenSelectCellsAmountDialog -> _state.update {
                    it.copy(openCellsAmountSelect = event.openDialog)
                }
            }
        }
    }
}

sealed interface CreatorsScreenEvent {
    data object LoadData : CreatorsScreenEvent
    data object LoadFollows : CreatorsScreenEvent
    data class LoadFollowers(val creatorID: String? = null) : CreatorsScreenEvent
    data class SetSearchName(val searchString: String) : CreatorsScreenEvent
    data class SetOpenSearchString(val openSearchString: Boolean) : CreatorsScreenEvent
    data class SetOpenSelectCellsAmountDialog(val openDialog : Boolean) : CreatorsScreenEvent
    data class SetCellsAmount(val cellsAmount : CellsAmount) : CreatorsScreenEvent
}
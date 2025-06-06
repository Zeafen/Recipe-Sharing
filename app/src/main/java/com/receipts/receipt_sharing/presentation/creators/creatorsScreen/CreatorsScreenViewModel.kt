package com.receipts.receipt_sharing.presentation.creators.creatorsScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.receipts.receipt_sharing.data.repositoriesImpl.AuthDataStoreRepository
import com.receipts.receipt_sharing.domain.creators.CreatorRequest
import com.receipts.receipt_sharing.domain.repositories.CreatorsRepository
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

class CreatorsScreenViewModel(
    private val creatorsRepo: CreatorsRepository,
) : ViewModel() {

    private val authDataStore = AuthDataStoreRepository.get()
    private var loadingJob : Job? = null

    private val _creators =
        MutableStateFlow<ApiResult<List<CreatorRequest>>>(ApiResult.Downloading())

    private val _state = MutableStateFlow(CreatorsScreenState())

    val state = combine(_creators, _state) { creators, state ->
        state.copy(creators = creators)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(300), CreatorsScreenState())

    /**
     * Processes Creators screen events
     * @param event Creators screen event
     * @see [CreatorsScreenEvent]
     */
    fun onEvent(event: CreatorsScreenEvent) {
        viewModelScope.launch {
            when (event) {
                CreatorsScreenEvent.LoadData -> {
                    if(loadingJob?.isActive == true)
                        loadingJob?.cancel()
                    loadingJob = launch {
                        _creators.update {
                            ApiResult.Downloading()
                        }
                        val creators = authDataStore.authDataStoreFlow.first().token?.let { tok ->
                            when (state.value.loadDataType) {
                                CreatorLoadDataType.All -> {
                                    if (state.value.searchedName.isEmpty())
                                        creatorsRepo.getCreators(
                                            tok,
                                            state.value.currentPage,
                                            state.value.pageSize.pageSize
                                        )
                                    else
                                        creatorsRepo.getCreatorsByName(
                                            tok,
                                            state.value.searchedName,
                                            state.value.currentPage,
                                            state.value.pageSize.pageSize
                                        )
                                }

                                is CreatorLoadDataType.Followers -> {
                                    (state.value.loadDataType as CreatorLoadDataType.Followers).creatorID?.let {
                                        if (state.value.searchedName.isEmpty())
                                            creatorsRepo.getCreatorFollowers(
                                                tok,
                                                it,
                                                state.value.currentPage,
                                                state.value.pageSize.pageSize
                                            )
                                        else
                                            creatorsRepo.getCreatorFollowersByName(
                                                tok,
                                                state.value.searchedName,
                                                it,
                                                state.value.currentPage,
                                                state.value.pageSize.pageSize
                                            )
                                    } ?: if (state.value.searchedName.isEmpty())
                                        creatorsRepo.getOwnFollowers(
                                            tok,
                                            state.value.currentPage,
                                            state.value.pageSize.pageSize
                                        )
                                    else
                                        creatorsRepo.getOwnFollowersByName(
                                            tok,
                                            state.value.searchedName,
                                            state.value.currentPage,
                                            state.value.pageSize.pageSize
                                        )
                                }

                                is CreatorLoadDataType.Follows -> {
                                    (state.value.loadDataType as CreatorLoadDataType.Follows).creatorID?.let {
                                        if (state.value.searchedName.isEmpty())
                                            creatorsRepo.getCreatorFollows(
                                                tok,
                                                it,
                                                state.value.currentPage,
                                                state.value.pageSize.pageSize
                                            )
                                        else
                                            creatorsRepo.getCreatorFollowsByName(
                                                tok,
                                                state.value.searchedName,
                                                it,
                                                state.value.currentPage,
                                                state.value.pageSize.pageSize
                                            )
                                    } ?: if (state.value.searchedName.isEmpty())
                                        creatorsRepo.getOwnFollows(
                                            tok,
                                            state.value.currentPage,
                                            state.value.pageSize.pageSize
                                        )
                                    else
                                        creatorsRepo.getOwnFollowsByName(
                                            tok,
                                            state.value.searchedName,
                                            state.value.currentPage,
                                            state.value.pageSize.pageSize
                                        )
                                }
                            }
                        } ?: ApiResult.Error("Cannot find account info")

                        when (creators) {
                            is ApiResult.Downloading -> {}
                            is ApiResult.Error -> _creators.update {
                                ApiResult.Error(creators.info ?: "Unknown error")
                            }

                            is ApiResult.Succeed -> {
                                _state.update {
                                    it.copy(
                                        currentPage = creators.data?.currentPage ?: it.currentPage,
                                        maxPages = creators.data?.totalPages ?: it.currentPage
                                    )
                                }
                                _creators.update {
                                    ApiResult.Succeed(creators.data?.result)
                                }
                            }
                        }
                    }
                }

                is CreatorsScreenEvent.SetLoadDataType -> {
                    when{
                        state.value.loadDataType != event.loadDataType -> {
                            _state.update {
                                it.copy(
                                    currentPage = 1,
                                    maxPages = 1,
                                    pageSize = PageSizes.Standard,
                                    searchedName = "",
                                    loadDataType = event.loadDataType
                                )
                            }
                            onEvent(CreatorsScreenEvent.LoadData)
                        }
                        state.value.creators !is ApiResult.Succeed -> {
                            onEvent(CreatorsScreenEvent.LoadData)
                        }
                    }
                }

                is CreatorsScreenEvent.SetSearchName -> {
                    _state.update {
                        it.copy(searchedName = event.searchString)
                    }
                    onEvent(CreatorsScreenEvent.LoadData)
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

                is CreatorsScreenEvent.SetCurrentPage -> {
                    _state.update {
                        it.copy(currentPage = event.currentPage)
                    }
                    onEvent(CreatorsScreenEvent.LoadData)
                }
            }
        }
    }
}
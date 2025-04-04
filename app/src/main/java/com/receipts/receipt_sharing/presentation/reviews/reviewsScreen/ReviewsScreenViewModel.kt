package com.receipts.receipt_sharing.presentation.reviews.reviewsScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.receipts.receipt_sharing.data.repositoriesImpl.AuthDataStoreRepository
import com.receipts.receipt_sharing.domain.repositories.CreatorsRepository
import com.receipts.receipt_sharing.domain.repositories.ReviewsRepository
import com.receipts.receipt_sharing.domain.response.ApiResult
import com.receipts.receipt_sharing.domain.reviews.OrderRequest
import com.receipts.receipt_sharing.domain.reviews.ReviewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReviewsScreenViewModel(
    private val reviewsRepo: ReviewsRepository,
    private val creatorsRepo: CreatorsRepository,
) : ViewModel() {
    private val authDataStoreRepo = AuthDataStoreRepository.get()
    private val _reviews: MutableStateFlow<ApiResult<List<ReviewModel>>> =
        MutableStateFlow(ApiResult.Downloading())
    private val _ownReviews: MutableStateFlow<ApiResult<ReviewModel>> =
        MutableStateFlow(ApiResult.Downloading())
    private val _state: MutableStateFlow<ReviewsScreenState> =
        MutableStateFlow(ReviewsScreenState())

    val state: StateFlow<ReviewsScreenState> =
        combine(_state, _reviews, _ownReviews) { state, reviews, ownReviews ->
            state.copy(reviews = reviews, ownReview = ownReviews)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    /**
     * Processes Reviews screen events
     * @param event Reviews screen event
     * @see [ReviewsScreenEvent]
     */
    fun onEvent(event: ReviewsScreenEvent) {
        viewModelScope.launch {
            when (event) {
                is ReviewsScreenEvent.DeleteReview -> {
                    authDataStoreRepo.authDataStoreFlow.first().token?.let { tok ->
                        val result = reviewsRepo.deleteReview(tok, event.reviewID)
                        _state.update {
                            it.copy(
                                infoMessage =
                                if (result is ApiResult.Succeed)
                                    "Review successfully deleted!!"
                                else result.info ?: "Cannot delete review. Unknown error!"
                            )
                        }
                    } ?: _state.update {
                        it.copy(infoMessage = "Cannot find account info.")
                    }
                    launch {
                        _ownReviews.update {
                            ApiResult.Downloading()
                        }
                        _ownReviews.update {
                            authDataStoreRepo.authDataStoreFlow.first().token?.let { tok ->
                                when (val review =
                                    reviewsRepo.getOwnReviewByRecipe(
                                        tok,
                                        state.value.selectedRecipeID
                                    )) {
                                    is ApiResult.Downloading -> ApiResult.Downloading()
                                    is ApiResult.Error -> ApiResult.Error(review.info)
                                    is ApiResult.Succeed -> ApiResult.Succeed(review.data?.let { rev ->
                                        creatorsRepo.getCreatorById(
                                            tok,
                                            rev.userID
                                        ).data?.let { creator ->
                                            ReviewModel(
                                                id = rev._id,
                                                userName = creator.nickname,
                                                userImageUrl = creator.imageUrl,
                                                text = rev.text,
                                                rating = rev.rating
                                            )
                                        }
                                    })
                                }
                            } ?: ApiResult.Error("Cannot find token info")
                        }
                    }
                }

                is ReviewsScreenEvent.LoadReviews -> {
                    _reviews.update {
                        ApiResult.Downloading()
                    }
                    if (state.value.selectedRecipeID != event.recipeID)
                        _state.update {
                            it.copy(
                                selectedRecipeID = event.recipeID,
                                currentPage = 1
                            )
                        }
                    authDataStoreRepo.authDataStoreFlow.first().token?.let { tok ->
                        when (val reviews =
                            reviewsRepo.getReviewsByRecipe(
                                tok,
                                event.recipeID,
                                state.value.currentPage,
                                state.value.pageSize.pageSize
                            )) {
                            is ApiResult.Downloading -> {}
                            is ApiResult.Error ->
                                _reviews.update {
                                    ApiResult.Error(reviews.info)
                                }

                            is ApiResult.Succeed -> {
                                _state.update {
                                    it.copy(
                                        currentPage = reviews.data?.currentPage ?: it.currentPage,
                                        totalPages = reviews.data?.totalPages ?: it.currentPage
                                    )
                                }
                                _reviews.update {
                                    ApiResult.Succeed(reviews.data?.result?.mapNotNull {
                                        creatorsRepo.getCreatorById(
                                            tok,
                                            it.userID
                                        ).data?.let { creator ->
                                            ReviewModel(
                                                id = it._id,
                                                userName = creator.nickname,
                                                userImageUrl = creator.imageUrl,
                                                text = it.text,
                                                rating = it.rating
                                            )
                                        }
                                    })
                                }
                            }
                        }
                    } ?: _reviews.update {
                        ApiResult.Error("Cannot find token info")
                    }
                    launch {
                        _ownReviews.update {
                            ApiResult.Downloading()
                        }
                        _ownReviews.update {
                            authDataStoreRepo.authDataStoreFlow.first().token?.let { tok ->
                                when (val review =
                                    reviewsRepo.getOwnReviewByRecipe(tok, event.recipeID)) {
                                    is ApiResult.Downloading -> ApiResult.Downloading()
                                    is ApiResult.Error -> ApiResult.Error(review.info)
                                    is ApiResult.Succeed -> ApiResult.Succeed(review.data?.let { rev ->
                                        creatorsRepo.getCreatorById(
                                            tok,
                                            rev.userID
                                        ).data?.let { creator ->
                                            ReviewModel(
                                                id = rev._id,
                                                userName = creator.nickname,
                                                userImageUrl = creator.imageUrl,
                                                text = rev.text,
                                                rating = rev.rating
                                            )
                                        }
                                    })
                                }
                            } ?: ApiResult.Error("Cannot find token info")
                        }
                    }
                }

                is ReviewsScreenEvent.SetCurrentPage -> {
                    _state.update {
                        it.copy(currentPage = event.currentPage)
                    }
                    onEvent(ReviewsScreenEvent.LoadReviews(state.value.selectedRecipeID))

                }

                is ReviewsScreenEvent.SetPageSize -> {
                    _state.update {
                        it.copy(pageSize = event.pageSize)
                    }
                }

                is ReviewsScreenEvent.SetOpenOrderingBox -> _state.update {
                    it.copy(openOrderingBox = event.openDialog)
                }

                is ReviewsScreenEvent.SetOpenSortingBox -> _state.update {
                    it.copy(openSortingBox = event.openDialog)
                }

                is ReviewsScreenEvent.SetOrdering -> _state.update {
                    if (state.value.selectedOrdering == event.ordering)
                        it.copy(isAscending = !state.value.isAscending)
                    else it.copy(
                        selectedOrdering = event.ordering,
                        isAscending = true
                    )
                }

                is ReviewsScreenEvent.SetSelectedReview -> {
                    authDataStoreRepo.authDataStoreFlow.first().token?.let { token ->
                        val owns = reviewsRepo.getIsOwnReview(token, event.reviewModel.id)
                        if (owns.data != null && owns.data)
                            _state.update {
                                it.copy(selectedReview = event.reviewModel)
                            }
                    }
                }

                is ReviewsScreenEvent.SetSorting -> _state.update {
                    it.copy(selectedSorting = event.filtering)
                }

                ReviewsScreenEvent.ClearMessage -> _state.update {
                    it.copy(infoMessage = "")
                }

                ReviewsScreenEvent.ApplyFilters -> {
                    _state.update {
                        it.copy(currentPage = 1)
                    }
                    _reviews.update {
                        ApiResult.Downloading()
                    }
                    authDataStoreRepo.authDataStoreFlow.first().token?.let { tok ->
                        when (val reviews =
                            when (state.value.selectedSorting) {
                                ReviewsSorting.NegativeOnly -> reviewsRepo.getOrderedNegReviewsByRecipe(
                                    tok,
                                    state.value.selectedRecipeID,
                                    OrderRequest(
                                        state.value.selectedOrdering,
                                        state.value.isAscending
                                    ),
                                    state.value.currentPage,
                                    state.value.pageSize.pageSize
                                )

                                ReviewsSorting.PositiveOnly -> reviewsRepo.getOrderedPosReviewsByRecipe(
                                    tok,
                                    state.value.selectedRecipeID,
                                    OrderRequest(
                                        state.value.selectedOrdering,
                                        state.value.isAscending
                                    ),
                                    state.value.currentPage,
                                    state.value.pageSize.pageSize
                                )

                                ReviewsSorting.All -> reviewsRepo.getOrderedReviewsByRecipe(
                                    tok,
                                    state.value.selectedRecipeID,
                                    OrderRequest(
                                        state.value.selectedOrdering,
                                        state.value.isAscending
                                    ),
                                    state.value.currentPage,
                                    state.value.pageSize.pageSize
                                )
                            }) {
                            is ApiResult.Downloading -> {}
                            is ApiResult.Error -> _reviews.update {
                                ApiResult.Error(reviews.info)
                            }

                            is ApiResult.Succeed -> {
                                _state.update {
                                    it.copy(
                                        currentPage = reviews.data?.currentPage
                                            ?: state.value.currentPage,
                                        totalPages = reviews.data?.currentPage
                                            ?: state.value.currentPage
                                    )
                                }
                                _reviews.update {
                                    ApiResult.Succeed(reviews.data?.result?.mapNotNull {
                                        creatorsRepo.getCreatorById(
                                            tok,
                                            it.userID
                                        ).data?.let { creator ->
                                            ReviewModel(
                                                id = it._id,
                                                userName = creator.nickname,
                                                userImageUrl = creator.imageUrl,
                                                text = it.text,
                                                rating = it.rating
                                            )
                                        }
                                    })
                                }
                            }
                        }
                    } ?: _reviews.update {
                        ApiResult.Error("Cannot find account info")
                    }
                }

                ReviewsScreenEvent.ClearFilters -> {
                    _state.update {
                        it.copy(
                            selectedSorting = ReviewsSorting.All,
                            selectedOrdering = ReviewsOrdering.None
                        )
                    }
                    onEvent(ReviewsScreenEvent.ApplyFilters)
                }

                ReviewsScreenEvent.OpenConfirmDeleteDialog -> _state.update {
                    it.copy(openConfirmDeleteDialog = true)
                }

                ReviewsScreenEvent.CloseDialogs -> _state.update {
                    it.copy(
                        openConfirmDeleteDialog = false,
                        openSortingBox = false,
                        openOrderingBox = false,
                        selectedReview = ReviewModel("", "", "", "", 0)
                    )
                }
            }
        }

    }
}
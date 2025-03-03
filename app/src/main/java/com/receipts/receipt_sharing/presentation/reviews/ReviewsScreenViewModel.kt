package com.receipts.receipt_sharing.presentation.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.receipts.receipt_sharing.data.repositoriesImpl.AuthDataStoreRepository
import com.receipts.receipt_sharing.domain.repositories.ICreatorsRepository
import com.receipts.receipt_sharing.domain.repositories.IReviewsRepository
import com.receipts.receipt_sharing.domain.response.RecipeResult
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
    private val reviewsRepo: IReviewsRepository,
    private val creatorsRepo: ICreatorsRepository,
) : ViewModel() {
    private val authDataStoreRepo = AuthDataStoreRepository.get()
    private val _reviews: MutableStateFlow<RecipeResult<List<ReviewModel>>> =
        MutableStateFlow(RecipeResult.Downloading())
    private val _ownReviews: MutableStateFlow<RecipeResult<ReviewModel>> =
        MutableStateFlow(RecipeResult.Downloading())
    private val _state: MutableStateFlow<ReviewsScreenState> =
        MutableStateFlow(ReviewsScreenState())

    val state: StateFlow<ReviewsScreenState> = combine(_state, _reviews) { state, reviews ->
        state.copy(reviews = reviews)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)


    fun onEvent(event: ReviewsScreenEvent) {
        viewModelScope.launch {
            when (event) {
                is ReviewsScreenEvent.DeleteReview -> {
                    authDataStoreRepo.authDataStoreFlow.first().token?.let { tok ->
                        val result = reviewsRepo.deleteReview(tok, event.reviewID)
                        _state.update {
                            it.copy(
                                infoMessage =
                                if (result is RecipeResult.Succeed)
                                    "Review successfully deleted!!"
                                else result.info ?: "Cannot delete review. Unknown error!"
                            )
                        }
                    } ?: _state.update {
                        it.copy(infoMessage = "Cannot find account info.")
                    }
                }

                is ReviewsScreenEvent.LoadReviews -> {
                    _reviews.update {
                        RecipeResult.Downloading()
                    }
                    _ownReviews.update {
                        RecipeResult.Downloading()
                    }
                    _state.update {
                        it.copy(
                            selectedRecipeID = event.recipeID
                        )
                    }
                    launch {
                        _reviews.update {
                            authDataStoreRepo.authDataStoreFlow.first().token?.let { tok ->
                                when (val reviews =
                                    reviewsRepo.getReviewsByRecipe(tok, event.recipeID)) {
                                    is RecipeResult.Downloading -> RecipeResult.Downloading()
                                    is RecipeResult.Error -> RecipeResult.Error(reviews.info)
                                    is RecipeResult.Succeed -> RecipeResult.Succeed(reviews.data?.mapNotNull {
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
                            } ?: RecipeResult.Error("Cannot find token info")
                        }
                    }

                    launch {
                        _ownReviews.update {
                            authDataStoreRepo.authDataStoreFlow.first().token?.let { tok ->
                                when (val review =
                                    reviewsRepo.getOwnReviewByRecipe(tok, event.recipeID)) {
                                    is RecipeResult.Downloading -> RecipeResult.Downloading()
                                    is RecipeResult.Error -> RecipeResult.Error(review.info)
                                    is RecipeResult.Succeed -> RecipeResult.Succeed(review.data?.let { rev ->
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
                            } ?: RecipeResult.Error("Cannot find token info")
                        }
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
                    else it.copy(selectedOrdering = event.ordering)
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
                    _reviews.update {
                        RecipeResult.Downloading()
                    }
                    _ownReviews.update {
                        RecipeResult.Downloading()
                    }

                    launch {
                        _reviews.update {
                            authDataStoreRepo.authDataStoreFlow.first().token?.let { tok ->
                                when (val reviews =
                                    when (state.value.selectedSorting) {
                                        ReviewsSorting.NegativeOnly -> reviewsRepo.getOrderedNegReviewsByRecipe(
                                            tok,
                                            state.value.selectedRecipeID,
                                            OrderRequest(
                                                state.value.selectedOrdering,
                                                state.value.isAscending
                                            )
                                        )

                                        ReviewsSorting.PositiveOnly -> reviewsRepo.getOrderedPosReviewsByRecipe(
                                            tok,
                                            state.value.selectedRecipeID,
                                            OrderRequest(
                                                state.value.selectedOrdering,
                                                state.value.isAscending
                                            )
                                        )

                                        ReviewsSorting.All -> reviewsRepo.getOrderedReviewsByRecipe(
                                            tok,
                                            state.value.selectedRecipeID,
                                            OrderRequest(
                                                state.value.selectedOrdering,
                                                state.value.isAscending
                                            )
                                        )
                                    }) {

                                    is RecipeResult.Downloading -> RecipeResult.Downloading()
                                    is RecipeResult.Error -> RecipeResult.Error(reviews.info)
                                    is RecipeResult.Succeed -> RecipeResult.Succeed(reviews.data?.mapNotNull {
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
                            } ?: RecipeResult.Error("Cannot find account info")
                        }
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
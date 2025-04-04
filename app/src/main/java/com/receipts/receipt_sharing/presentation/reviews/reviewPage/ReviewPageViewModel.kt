package com.receipts.receipt_sharing.presentation.reviews.reviewPage

import RecipesRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.receipts.receipt_sharing.data.repositoriesImpl.AuthDataStoreRepository
import com.receipts.receipt_sharing.domain.repositories.ReviewsRepository
import com.receipts.receipt_sharing.domain.response.ApiResult
import com.receipts.receipt_sharing.domain.reviews.RecipeReview
import com.receipts.receipt_sharing.domain.reviews.ReviewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReviewPageViewModel(
    private val recipesRepo: RecipesRepository,
    private val reviewsRepo: ReviewsRepository,
) : ViewModel() {
    private val authDataStoreRepo = AuthDataStoreRepository.get()

    private val _review: MutableStateFlow<ApiResult<ReviewModel>> =
        MutableStateFlow(ApiResult.Downloading())
    private val _state: MutableStateFlow<ReviewPageState> = MutableStateFlow(ReviewPageState())

    val state: StateFlow<ReviewPageState> = combine(_state, _review) { state, review ->
        state.copy(review = review)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    init {
        viewModelScope.launch {
            authDataStoreRepo.authDataStoreFlow.collect { authFlow ->
                _state.update {
                    it.copy(
                        userName = authFlow.userName,
                        userImageUrl = authFlow.imageUrl ?: ""
                    )
                }
            }
        }
    }

    /**
     * Processes Review info page events
     * @param event Review info page event
     * @see [ReviewPageEvent]
     */
    fun onEvent(event: ReviewPageEvent) {
        viewModelScope.launch {
            when (event) {
                ReviewPageEvent.ConfirmChanges -> {
                    val reviewText = state.value.reviewText
                    val reviewRating = state.value.reviewRating

                    if (reviewText.isBlank() || reviewText.length < 50 || reviewText.split(" ").size < 5) {
                        _state.update {
                            it.copy(infoMessage = "Inappropriate review text")
                        }
                        return@launch
                    } else if (reviewRating !in 1..5) {
                        _state.update {
                            it.copy(infoMessage = "Inappropriate review rating")
                        }
                        return@launch
                    }


                    authDataStoreRepo.authDataStoreFlow.first().token?.let { tok ->
                        state.value.review.data?.let { review ->
                            val result = if (review.id.isNotEmpty())
                                reviewsRepo.updateReview(
                                    tok, RecipeReview(
                                        review.id,
                                        state.value.selectedRecipeID,
                                        "",
                                        reviewText,
                                        reviewRating
                                    )
                                )
                            else reviewsRepo.postReview(
                                tok, RecipeReview(
                                    review.id,
                                    state.value.selectedRecipeID,
                                    "",
                                    reviewText,
                                    reviewRating
                                )
                            )

                            _state.update {
                                if (result is ApiResult.Error)
                                    it.copy(infoMessage = result.info)
                                else it.copy(infoMessage = "Review posted")
                            }
                        }
                    }
                }

                is ReviewPageEvent.LoadReviewByRecipe -> {
                    _review.update {
                        ApiResult.Downloading()
                    }

                    authDataStoreRepo.authDataStoreFlow.first().token?.let { tok ->
                        launch {
                            val owns = reviewsRepo.getOwnReviewByRecipe(tok, event.recipeID)
                            _review.update {
                                when (owns) {
                                    is ApiResult.Succeed -> {
                                        _state.update {
                                            it.copy(selectedRecipeID = event.recipeID)
                                        }
                                        ApiResult.Succeed(
                                            owns.data?.let {
                                                ReviewModel(
                                                    it._id,
                                                    authDataStoreRepo.authDataStoreFlow.first().userName,
                                                    authDataStoreRepo.authDataStoreFlow.first().imageUrl
                                                        ?: "",
                                                    it.text, it.rating
                                                )
                                            } ?: ReviewModel(
                                                "",
                                                authDataStoreRepo.authDataStoreFlow.first().userName,
                                                authDataStoreRepo.authDataStoreFlow.first().imageUrl
                                                    ?: "",
                                                "",
                                                0
                                            )
                                        )
                                    }

                                    else -> ApiResult.Error(owns.info)
                                }
                            }
                            _state.update {
                                it.copy(
                                    reviewText = owns.data?.text ?: "",
                                    reviewRating = owns.data?.rating ?: 0
                                )
                            }
                        }
                        launch {
                            _state.update {
                                recipesRepo.getRecipeByID(tok, event.recipeID).data?.let { recipe ->
                                    it.copy(
                                        recipeName = recipe.recipeName,
                                        recipeImageUrl = recipe.imageUrl ?: ""
                                    )
                                } ?: it.copy(
                                    infoMessage = "Recipe not found"
                                )
                            }
                        }
                    }
                }

                is ReviewPageEvent.SetReviewRating -> _state.update {
                    it.copy(
                        reviewRating = event.rating,
                        isError = state.value.reviewRating !in 1..5 && state.value.reviewText.length < 100 && state.value.reviewText.split(
                            " "
                        ).size < 15
                    )
                }

                is ReviewPageEvent.SetReviewText -> _state.update {
                    it.copy(
                        reviewText = event.text,
                        isError = state.value.reviewRating !in 1..5 && event.text.length < 50 && event.text.split(
                            " "
                        ).size < 5
                    )
                }

                is ReviewPageEvent.LoadReview -> {
                    _review.update {
                        ApiResult.Downloading()
                    }

                    authDataStoreRepo.authDataStoreFlow.first().token?.let { tok ->
                        val review = reviewsRepo.getReviewByID(tok, event.reviewID)
                        _review.update {
                            when (review) {
                                is ApiResult.Succeed -> ApiResult.Succeed(
                                    review.data?.let {
                                        ReviewModel(
                                            it._id,
                                            authDataStoreRepo.authDataStoreFlow.first().userName,
                                            authDataStoreRepo.authDataStoreFlow.first().imageUrl
                                                ?: "",
                                            it.text, it.rating
                                        )
                                    } ?: ReviewModel(
                                        "",
                                        authDataStoreRepo.authDataStoreFlow.first().userName,
                                        authDataStoreRepo.authDataStoreFlow.first().imageUrl
                                            ?: "",
                                        "",
                                        0
                                    )
                                )

                                else -> ApiResult.Error(review.info)
                            }
                        }
                        _review.value.data?.let { review ->
                            _state.update {
                                it.copy(
                                    reviewRating = review.rating,
                                    reviewText = review.text
                                )
                            }
                        }
                        launch {
                            review.data?.let {
                                _state.update {
                                    recipesRepo.getRecipeByID(
                                        tok,
                                        review.data._id
                                    ).data?.let { recipe ->
                                        it.copy(
                                            recipeName = recipe.recipeName,
                                            recipeImageUrl = recipe.imageUrl ?: ""
                                        )
                                    } ?: it.copy(
                                        infoMessage = "Recipe not found"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
package com.receipts.receipt_sharing.presentation.reviews

import IRecipesRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.receipts.receipt_sharing.data.repositoriesImpl.AuthDataStoreRepository
import com.receipts.receipt_sharing.domain.repositories.ICreatorsRepository
import com.receipts.receipt_sharing.domain.repositories.IReviewsRepository
import com.receipts.receipt_sharing.domain.response.RecipeResult
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
    private val creatorsRepo: ICreatorsRepository,
    private val recipesRepo: IRecipesRepository,
    private val reviewsRepo: IReviewsRepository,
) : ViewModel() {
    private val authDataStoreRepo = AuthDataStoreRepository.get()

    private val _review: MutableStateFlow<RecipeResult<ReviewModel>> =
        MutableStateFlow(RecipeResult.Downloading())
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

    fun onEvent(event: ReviewPageEvent) {
        viewModelScope.launch {
            when (event) {
                ReviewPageEvent.ConfirmChanges -> {
                    authDataStoreRepo.authDataStoreFlow.first().token?.let { tok ->
                        state.value.review.data?.let { review ->
                            val result = if (review.id.isNotEmpty())
                                reviewsRepo.updateReview(
                                    tok, RecipeReview(
                                        review.id,
                                        "",
                                        state.value.selectedRecipeID,
                                        review.text,
                                        review.rating
                                    )
                                )
                            else reviewsRepo.postReview(
                                tok, RecipeReview(
                                    review.id,
                                    "",
                                    state.value.selectedRecipeID,
                                    review.text,
                                    review.rating
                                )
                            )

                            if (result is RecipeResult.Error)
                                _state.update {
                                    it.copy(infoMessage = result.info)
                                }
                        }
                    }
                }

                is ReviewPageEvent.LoadReviewByRecipe -> {
                    _review.update {
                        RecipeResult.Downloading()
                    }

                    authDataStoreRepo.authDataStoreFlow.first().token?.let { tok ->
                        launch {
                            val owns = reviewsRepo.getOwnReviewByRecipe(tok, event.recipeID)
                            _review.update {
                                when (owns) {
                                    is RecipeResult.Succeed -> RecipeResult.Succeed(
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

                                    else -> RecipeResult.Error(owns.info)
                                }
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
                        isError = state.value.reviewRating !in 1..5 && event.text.length < 100 && event.text.split(
                            " "
                        ).size < 15
                    )
                }

                is ReviewPageEvent.LoadReview -> {
                    _review.update {
                        RecipeResult.Downloading()
                    }

                    authDataStoreRepo.authDataStoreFlow.first().token?.let { tok ->
                        val review = reviewsRepo.getReviewByID(tok, event.reviewID)
                        _review.update {
                            when (review) {
                                is RecipeResult.Succeed -> RecipeResult.Succeed(
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

                                else -> RecipeResult.Error(review.info)
                            }
                        }
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
package com.receipts.receipt_sharing.presentation.reviews

import com.receipts.receipt_sharing.domain.response.RecipeResult
import com.receipts.receipt_sharing.domain.reviews.ReviewModel

data class ReviewsScreenState(
    val ownReview : RecipeResult<ReviewModel> = RecipeResult.Downloading(),
    val reviews : RecipeResult<List<ReviewModel>> = RecipeResult.Downloading(),
    val selectedReview :ReviewModel? = null,
    val selectedRecipeID : String = "",
    val recipeName : String = "",
    val recipeImageUrl : String = "",
    val recipeRating : Float = 0f,
    val openConfirmDeleteDialog : Boolean = false,
    val selectedSorting: ReviewsSorting = ReviewsSorting.All,
    val openSortingBox : Boolean = false,
    val selectedOrdering : ReviewsOrdering = ReviewsOrdering.None,
    val openOrderingBox : Boolean = false,
    val isAscending : Boolean = true,
    val infoMessage : String = ""
)

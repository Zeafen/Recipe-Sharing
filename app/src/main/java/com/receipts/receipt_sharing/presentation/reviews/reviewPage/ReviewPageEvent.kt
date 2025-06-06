package com.receipts.receipt_sharing.presentation.reviews.reviewPage

sealed interface ReviewPageEvent {
    data object Refresh : ReviewPageEvent
    data object GoBack : ReviewPageEvent
    data class LoadReviewByRecipe(val recipeID : String) : ReviewPageEvent
    data class LoadReview(val reviewID : String) : ReviewPageEvent
    data class SetReviewText(val text : String) : ReviewPageEvent
    data class SetReviewRating(val rating : Int) : ReviewPageEvent
    data object ConfirmChanges : ReviewPageEvent
}
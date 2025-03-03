package com.receipts.receipt_sharing.presentation.reviews

import com.receipts.receipt_sharing.domain.reviews.ReviewModel

sealed interface ReviewsScreenEvent {
    data class LoadReviews(val recipeID: String) : ReviewsScreenEvent
    data class SetOrdering(val ordering: ReviewsOrdering) : ReviewsScreenEvent
    data class SetSorting(val filtering: ReviewsSorting) : ReviewsScreenEvent
    data class SetOpenOrderingBox(val openDialog : Boolean) : ReviewsScreenEvent
    data class SetOpenSortingBox(val openDialog: Boolean) : ReviewsScreenEvent
    data object OpenConfirmDeleteDialog : ReviewsScreenEvent
    data object ApplyFilters : ReviewsScreenEvent
    data object ClearFilters : ReviewsScreenEvent
    data class SetSelectedReview(val reviewModel : ReviewModel) : ReviewsScreenEvent
    data class DeleteReview(val reviewID : String) : ReviewsScreenEvent
    data object ClearMessage : ReviewsScreenEvent
    data object CloseDialogs : ReviewsScreenEvent
}
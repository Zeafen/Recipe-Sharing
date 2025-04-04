package com.receipts.receipt_sharing.domain.reviews

import com.receipts.receipt_sharing.presentation.reviews.reviewsScreen.ReviewsOrdering
import kotlinx.serialization.Serializable

@Serializable
data class OrderRequest(
    val ordering : ReviewsOrdering,
    val iAscending : Boolean
)
package com.receipts.receipt_sharing.domain.reviews

import com.receipts.receipt_sharing.presentation.reviews.ReviewsOrdering

data class OrderRequest(
    val ordering : ReviewsOrdering,
    val iAscending : Boolean
)

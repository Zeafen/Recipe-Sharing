package com.receipts.receipt_sharing.presentation.reviews

import androidx.annotation.StringRes
import com.receipts.receipt_sharing.R

enum class ReviewsSorting(@StringRes val nameRes : Int) {
    NegativeOnly(R.string.reviews_sorting_negative),
    PositiveOnly(R.string.reviews_sorting_positive),
    All(R.string.reviews_sorting_all)
}

enum class ReviewsOrdering(@StringRes val nameRes : Int){
    None(R.string.reviews_ordering_none),
    Rating(R.string.reviews_ordering_rate),
    TextLength(R.string.reviews_ordering_text),
}
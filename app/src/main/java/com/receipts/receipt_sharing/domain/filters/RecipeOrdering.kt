package com.receipts.receipt_sharing.domain.filters

import androidx.annotation.StringRes
import com.receipts.receipt_sharing.R

enum class RecipeOrdering(@StringRes val nameRes : Int) {
    DatePublished(R.string.recipe_ordering_date),
    Rating(R.string.recipe_ordering_rating),
}
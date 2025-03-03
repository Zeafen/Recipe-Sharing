package com.receipts.receipt_sharing.presentation.recipes

import androidx.annotation.StringRes
import com.receipts.receipt_sharing.R

enum class RecipeTab(@StringRes val tabName : Int) {
    Info(R.string.recipe_info_tab),
    Ingredients(R.string.recipe_tab_ingredients),
    Instructions(R.string.recipe_tab_instruction),
    Reviews(R.string.recipe_tab_reviews),
}
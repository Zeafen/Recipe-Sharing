package com.receipts.receipt_sharing.presentation.recipes

import androidx.annotation.StringRes
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.response.RecipeResult

data class RecipesScreenState(
    val recipes : RecipeResult<List<Recipe>> = RecipeResult.Downloading(),
    val filters : Map<String, List<String>> = mapOf(),
    val savedFilters : List<String> = emptyList(),
    val selectedCreatorID : String = "",
    val cellsCount: CellsAmount = CellsAmount.One,
    val searchString : String = "",
    val favoritesLoaded : Boolean = false,
    val creatorLoaded : Boolean = false,
    val openSearch : Boolean = false,
    val openSelectColumnMenu : Boolean = false
)

enum class CellsAmount(val cellsCount : Int, @StringRes val nameRes : Int){
    One(1, R.string.one_column_name),
    Two(2, R.string.two_column_name),
    Three(3, R.string.three_column_name)
}
package com.receipts.receipt_sharing.ui.recipe

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
    val creatorLoaded : Boolean = false
)

enum class CellsAmount(val cellsCount : Int){
    One(1),
    Two(2),
    Three(3)
}
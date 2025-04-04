package com.receipts.receipt_sharing.presentation.recipes.recipesScreen

sealed interface RecipesLoadedDataType{
    data object OwnDataRecipes : RecipesLoadedDataType
    data class CreatorRecipes(val creatorID : String) : RecipesLoadedDataType
    data object Favorites : RecipesLoadedDataType
    data class Similar(val recipeID : String) : RecipesLoadedDataType
    data object All : RecipesLoadedDataType
}
package com.receipts.receipt_sharing.ui.recipe

import com.receipts.receipt_sharing.data.recipes.Ingredient
import com.receipts.receipt_sharing.data.recipes.Recipe
import com.receipts.receipt_sharing.data.recipes.Step
import com.receipts.receipt_sharing.data.response.RecipeResult

data class RecipePageState(
    val recipe : RecipeResult<Recipe> = RecipeResult.Downloading(),
    val imageUrl : RecipeResult<String> = RecipeResult.Downloading(),
    val recipeName : String = "",
    val recipeDescription : String = "",
    val ingredients : List<Ingredient> = emptyList(),
    val steps : List<Step> = emptyList(),
    val own : Boolean = false,
    val isFavorite: Boolean = false,
    val filters : RecipeResult<List<String>> = RecipeResult.Downloading()
)

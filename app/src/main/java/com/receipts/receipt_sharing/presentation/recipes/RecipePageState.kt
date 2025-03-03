package com.receipts.receipt_sharing.presentation.recipes

import com.receipts.receipt_sharing.domain.recipes.Ingredient
import com.receipts.receipt_sharing.domain.recipes.Measure
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.recipes.Step
import com.receipts.receipt_sharing.domain.response.RecipeResult
import com.receipts.receipt_sharing.domain.reviews.ReviewModel

data class RecipePageState(
    val recipe : RecipeResult<Recipe> = RecipeResult.Downloading(),
    val imageUrl : RecipeResult<String> = RecipeResult.Downloading(),
    val recipeName : String = "",
    val recipeDescription : String = "",
    val recipeRating : Float = 0f,
    val recipeReviewsCount : Long = 0,
    val own : Boolean = false,
    val isFavorite: Boolean = false,
    val ingredients : List<Ingredient> = emptyList(),
    val steps : List<Step> = emptyList(),
    val filters : RecipeResult<List<String>> = RecipeResult.Downloading(),
    val reviews : RecipeResult<List<ReviewModel>> = RecipeResult.Downloading(),
    val isEditingRecord : Boolean = false,
    val openAddIngredientDialog : Boolean = false,
    val openAddStepDialog : Boolean = false,
    val openConfirmDeleteDialog : Boolean = false,
    val selectedStep : Step = Step("", 0L),
    val selectedStepIndex : Int = -1,
    val selectedIngredient : Ingredient = Ingredient("", 0L, Measure.Gram),
    val selectedIngrIndex : Int = -1,
    val infoMessage : String? = null,
    val selectedRecipeTabIndex : Int = 0,
    val isError : Boolean = false,
)

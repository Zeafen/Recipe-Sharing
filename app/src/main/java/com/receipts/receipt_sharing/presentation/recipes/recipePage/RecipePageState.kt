package com.receipts.receipt_sharing.presentation.recipes.recipePage

import com.receipts.receipt_sharing.domain.recipes.Ingredient
import com.receipts.receipt_sharing.domain.recipes.Measure
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.recipes.Step
import com.receipts.receipt_sharing.domain.response.ApiResult
import com.receipts.receipt_sharing.domain.reviews.ReviewModel

data class  RecipePageState(
    val recipe : ApiResult<Recipe> = ApiResult.Downloading(),
    val imageUrl : String? = null,
    val recipeName : String = "",
    val recipeDescription : String = "",
    val recipeRating : Float = 0f,
    val recipeReviewsCount : Long = 0,
    val own : Boolean = false,
    val isFavorite: Boolean = false,
    val ingredients : List<Ingredient> = emptyList(),
    val steps : List<Step> = emptyList(),

    val filters : ApiResult<List<String>> = ApiResult.Downloading(),
    val loadedFilters : ApiResult<Map<String, List<String>>> = ApiResult.Downloading(),


    val reviews : ApiResult<List<ReviewModel>> = ApiResult.Downloading(),
    val isEditingRecord : Boolean = false,
    val openAddIngredientDialog : Boolean = false,
    val openFiltersPage : Boolean = false,
    val openAddStepDialog : Boolean = false,
    val openConfirmDeleteDialog : Boolean = false,
    val selectedStep : Step = Step("", 0L),
    val selectedStepIndex : Int = -1,
    val selectedIngredient : Ingredient = Ingredient("", 0f, Measure.Gram),
    val selectedIngrIndex : Int = -1,
    val infoMessage : String? = null,
    val selectedRecipeTabIndex : Int = 0,
    val isError : Boolean = false,
)
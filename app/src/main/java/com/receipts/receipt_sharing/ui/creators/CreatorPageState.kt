package com.receipts.receipt_sharing.ui.creators

import com.receipts.receipt_sharing.data.CreatorRequest
import com.receipts.receipt_sharing.data.recipes.Recipe
import com.receipts.receipt_sharing.data.response.RecipeResult

data class CreatorPageState(
    val creator : RecipeResult<CreatorRequest> = RecipeResult.Downloading(),
    val recipes : RecipeResult<List<Recipe>> = RecipeResult.Downloading(),
    val creatorName : String = "",
    val imageUrl : String? = "",
    val follows : Boolean = false,
    val followers : Int = 0
)
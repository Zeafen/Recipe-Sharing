package com.receipts.receipt_sharing.ui.creators

import com.receipts.receipt_sharing.domain.CreatorRequest
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.response.RecipeResult

data class CreatorPageState(
    val userInfoLoaded : Boolean = false,
    val creator : RecipeResult<CreatorRequest> = RecipeResult.Downloading(),
    val recipes : RecipeResult<List<Recipe>> = RecipeResult.Downloading(),
    val creatorName : String = "",
    val imageUrl : String? = "",
    val follows : Boolean = false,
    val followers : Int = 0
)
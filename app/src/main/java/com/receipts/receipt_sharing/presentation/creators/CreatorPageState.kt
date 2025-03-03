package com.receipts.receipt_sharing.presentation.creators

import com.receipts.receipt_sharing.domain.creators.CreatorRequest
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.response.RecipeResult

data class CreatorPageState(
    val creator : RecipeResult<CreatorRequest> = RecipeResult.Downloading(),
    val recipes : RecipeResult<List<Recipe>> = RecipeResult.Downloading(),
    val follows : Boolean = false,
    val followersCount : Long = 0,
    val followsCount : Long = 0
)
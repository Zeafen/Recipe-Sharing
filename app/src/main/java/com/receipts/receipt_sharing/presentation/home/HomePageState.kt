package com.receipts.receipt_sharing.presentation.home

import com.receipts.receipt_sharing.domain.creators.CreatorRequest
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.response.ApiResult

data class HomePageState(
    val topPublishers : ApiResult<List<CreatorRequest>> = ApiResult.Downloading(),
    val popularRecipes : ApiResult<List<Recipe>> = ApiResult.Downloading(),
    val recentRecipes : ApiResult<List<Recipe>> = ApiResult.Downloading(),
    val userName : String = "",
)
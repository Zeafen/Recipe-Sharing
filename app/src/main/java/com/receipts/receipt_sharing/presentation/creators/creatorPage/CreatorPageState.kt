package com.receipts.receipt_sharing.presentation.creators.creatorPage

import com.receipts.receipt_sharing.domain.creators.CreatorRequest
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.response.ApiResult

data class CreatorPageState(
    val creator : ApiResult<CreatorRequest> = ApiResult.Downloading(),
    val recipes : ApiResult<List<Recipe>> = ApiResult.Downloading(),
    val expandAboutMe : Boolean = true,
    val follows : Boolean = false,
    val followersCount : Long = 0,
    val followsCount : Long = 0
)
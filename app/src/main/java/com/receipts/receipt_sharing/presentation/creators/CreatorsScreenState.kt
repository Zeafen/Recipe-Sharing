package com.receipts.receipt_sharing.ui.creators

import com.receipts.receipt_sharing.domain.CreatorRequest
import com.receipts.receipt_sharing.domain.response.RecipeResult

data class CreatorsScreenState(
    val creators : RecipeResult<List<CreatorRequest>> = RecipeResult.Downloading(),
    val searchedName : String = "",
    val followsLoaded : Boolean = false
)
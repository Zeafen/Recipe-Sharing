package com.receipts.receipt_sharing.ui.creators

import com.receipts.receipt_sharing.data.CreatorRequest
import com.receipts.receipt_sharing.data.response.RecipeResult

data class CreatorsScreenState(
    val creators : RecipeResult<List<CreatorRequest>> = RecipeResult.Downloading(),
    val searchedName : String = "",
    val followsLoaded : Boolean = false
)
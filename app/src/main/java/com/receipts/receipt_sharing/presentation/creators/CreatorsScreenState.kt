package com.receipts.receipt_sharing.presentation.creators

import com.receipts.receipt_sharing.domain.creators.CreatorRequest
import com.receipts.receipt_sharing.domain.response.RecipeResult
import com.receipts.receipt_sharing.presentation.recipes.CellsAmount

data class CreatorsScreenState(
    val creators : RecipeResult<List<CreatorRequest>> = RecipeResult.Downloading(),
    val openSearchString : Boolean = false,
    val openCellsAmountSelect : Boolean = false,
    val cellsAmount : CellsAmount = CellsAmount.One,
    val searchedName : String = "",
    val followsLoaded : Boolean = false
)
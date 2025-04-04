package com.receipts.receipt_sharing.presentation.creators.creatorsScreen

import com.receipts.receipt_sharing.domain.creators.CreatorRequest
import com.receipts.receipt_sharing.domain.response.ApiResult
import com.receipts.receipt_sharing.presentation.CellsAmount
import com.receipts.receipt_sharing.presentation.PageSizes

data class CreatorsScreenState(
    val creators : ApiResult<List<CreatorRequest>> = ApiResult.Downloading(),
    val currentPage : Int = 1,
    val maxPages : Int = 1,
    val pageSize : PageSizes = PageSizes.Standard,
    val openSearchString : Boolean = false,
    val openCellsAmountSelect : Boolean = false,
    val cellsAmount : CellsAmount = CellsAmount.One,
    val searchedName : String = "",
    val loadDataType: CreatorLoadDataType = CreatorLoadDataType.All
)
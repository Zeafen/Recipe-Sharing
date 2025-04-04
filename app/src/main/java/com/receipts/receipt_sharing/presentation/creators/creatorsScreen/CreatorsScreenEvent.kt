package com.receipts.receipt_sharing.presentation.creators.creatorsScreen

import com.receipts.receipt_sharing.presentation.CellsAmount

sealed interface CreatorsScreenEvent {
    data object LoadData : CreatorsScreenEvent
    data class SetLoadDataType(val loadDataType: CreatorLoadDataType) : CreatorsScreenEvent
    data class SetSearchName(val searchString: String) : CreatorsScreenEvent
    data class SetOpenSearchString(val openSearchString: Boolean) : CreatorsScreenEvent
    data class SetOpenSelectCellsAmountDialog(val openDialog: Boolean) : CreatorsScreenEvent
    data class SetCellsAmount(val cellsAmount: CellsAmount) : CreatorsScreenEvent
    data class SetCurrentPage(val currentPage: Int) : CreatorsScreenEvent
}
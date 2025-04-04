package com.receipts.receipt_sharing.presentation

import androidx.annotation.StringRes
import com.receipts.receipt_sharing.R

enum class CellsAmount(val cellsCount : Int, @StringRes val nameRes : Int){
    One(1, R.string.one_column_name),
    Two(2, R.string.two_column_name),
    Three(3, R.string.three_column_name)
}
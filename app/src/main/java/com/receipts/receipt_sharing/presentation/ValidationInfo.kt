package com.receipts.receipt_sharing.presentation

import androidx.annotation.StringRes

data class ValidationInfo(
    val isValid : Boolean = false,
    @StringRes
    val errorInfoID : Int? = null,
    val formatArgs : List<Any> = emptyList()
)
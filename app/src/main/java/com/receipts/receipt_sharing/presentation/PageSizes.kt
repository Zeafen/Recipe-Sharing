package com.receipts.receipt_sharing.presentation

import androidx.annotation.StringRes
import com.receipts.receipt_sharing.R

enum class PageSizes(
    @StringRes val nameRes : Int,
    val pageSize : Int
) {
    Small(R.string.small_page_size_name, 10),
    Standard(R.string.standard_page_size_name, 20),
    Large(R.string.large_page_size_name, 40),
    ExtraLarge(R.string.extra_large_page_size_name, 64),
}
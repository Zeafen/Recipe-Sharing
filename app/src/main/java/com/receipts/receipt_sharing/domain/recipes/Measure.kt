package com.receipts.receipt_sharing.domain.recipes

import androidx.annotation.StringRes
import com.receipts.receipt_sharing.R
import kotlinx.serialization.Serializable

@Serializable
enum class Measure(@StringRes val shortName : Int,
                   @StringRes val fulName : Int){
    Liter(R.string.liter_name, R.string.liter_fulname),
    Milliliter(R.string.milliliter_name, R.string.milliliter_fulname),
    Gram(R.string.gram_name, R.string.gram_fulname),
    Kilogram(R.string.kilogram_name, R.string.kilogram_fulname),
}
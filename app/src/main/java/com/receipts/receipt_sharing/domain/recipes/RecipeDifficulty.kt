package com.receipts.receipt_sharing.domain.recipes

import androidx.annotation.StringRes
import com.receipts.receipt_sharing.R

enum class RecipeDifficulty(@StringRes val nameRes : Int){
    Beginner(R.string.beginner_dif_name),
    Common(R.string.common_dif_name),
    Adept(R.string.adept_dif_name),
    MasterPiece(R.string.masterpiece_dif_name)
}

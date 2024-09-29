package com.receipts.receipt_sharing.data.recipes

import kotlinx.serialization.Serializable

@Serializable
data class Step(
    val description : String,
    val duration : Long,
)
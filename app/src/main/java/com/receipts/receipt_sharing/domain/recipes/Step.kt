package com.receipts.receipt_sharing.domain.recipes

import kotlinx.serialization.Serializable

@Serializable
data class Step(
    val description : String,
    val duration : Long,
)
package com.receipts.receipt_sharing.domain.recipes

import kotlinx.serialization.Serializable

@Serializable
data class TimeRange(
    val timeFrom : Int,
    val timeTo : Int
)

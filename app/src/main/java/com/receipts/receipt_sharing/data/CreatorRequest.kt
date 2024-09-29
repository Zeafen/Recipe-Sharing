package com.receipts.receipt_sharing.data

import kotlinx.serialization.Serializable

@Serializable
data class CreatorRequest(
    val userID : String,
    val nickname : String,
    val imageUrl : String
)
package com.receipts.receipt_sharing.domain

import kotlinx.serialization.Serializable

@Serializable
data class CreatorRequest(
    val userID : String,
    val nickname : String,
    val imageUrl : String
)
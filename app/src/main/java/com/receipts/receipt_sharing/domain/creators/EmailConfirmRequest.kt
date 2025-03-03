package com.receipts.receipt_sharing.domain.creators

import kotlinx.serialization.Serializable

@Serializable
data class EmailConfirmRequest(
    val email : String,
    val code : String
)

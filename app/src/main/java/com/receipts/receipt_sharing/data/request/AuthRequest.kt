package com.receipts.receipt_sharing.data.request

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest (
    val username : String,
    val password : String
)
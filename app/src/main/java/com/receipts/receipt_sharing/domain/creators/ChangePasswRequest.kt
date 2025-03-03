package com.receipts.receipt_sharing.domain.creators

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswRequest(
    val email : String,
    val password : String,
    val emailCode : String
)

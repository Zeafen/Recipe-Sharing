package com.receipts.receipt_sharing.domain

import kotlinx.serialization.Serializable

@Serializable
data class ProfileRequest(
    val userID : String,
    val nickname : String,
    val email : String,
    val login : String,
    val password : String,
    val aboutMe : String,
    val imageUrl : String
)

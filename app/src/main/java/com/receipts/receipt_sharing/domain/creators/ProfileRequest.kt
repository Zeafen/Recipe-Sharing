package com.receipts.receipt_sharing.domain.creators

import kotlinx.serialization.Serializable

@Serializable
data class ProfileRequest(
    val userID : String,
    val nickname : String,
    val login : String,
    val password : String,
    val aboutMe : String,
    val imageUrl : String,
    val email : String,
    val emailConfirmed : Boolean
)

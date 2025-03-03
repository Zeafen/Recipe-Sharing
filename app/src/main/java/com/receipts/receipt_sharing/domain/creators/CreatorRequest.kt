package com.receipts.receipt_sharing.domain.creators

import kotlinx.serialization.Serializable

@Serializable
data class CreatorRequest(
    val userID : String,
    val nickname : String,
    val aboutMe : String?,
    val imageUrl : String,
    val recipesCount : Long = 0L,
    val followersCount: Long = 0L
)
package com.receipts.receipt_sharing.data.dataStore

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val userName : String = "",
    val token : String? = null,
    val imageUrl : String? = null
)

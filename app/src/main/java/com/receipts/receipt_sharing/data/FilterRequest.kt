package com.receipts.receipt_sharing.data

import kotlinx.serialization.Serializable

@Serializable
data class FilterRequest(
    val recipeID : String,
    val filters : List<String>
)

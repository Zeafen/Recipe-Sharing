package com.receipts.receipt_sharing.domain

import kotlinx.serialization.Serializable

@Serializable
data class FilterRequest(
    val recipeID : String,
    val filters : List<String>
)

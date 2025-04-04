package com.receipts.receipt_sharing.domain.filters

import kotlinx.serialization.Serializable

@Serializable
data class RecipeFilteringRequest(
    val filters : List<String>,
    val ordering: OrderingRequest? = null,
    val ingredients : List<String>? = null
)
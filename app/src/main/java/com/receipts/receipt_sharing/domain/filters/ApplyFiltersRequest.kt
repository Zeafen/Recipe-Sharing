package com.receipts.receipt_sharing.domain.filters

import kotlinx.serialization.Serializable

@Serializable
data class ApplyFiltersRequest(
    val recipeID : String,
    val filters : List<String>
)
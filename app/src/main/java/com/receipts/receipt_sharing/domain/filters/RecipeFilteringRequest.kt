package com.receipts.receipt_sharing.domain.filters

import kotlinx.serialization.Serializable

@Serializable
data class RecipeFilteringRequest(
    val ordering: OrderingRequest? = null,
    val filters : FiltersModel? = null
)
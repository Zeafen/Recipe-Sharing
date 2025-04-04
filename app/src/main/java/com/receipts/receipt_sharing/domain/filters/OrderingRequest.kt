package com.receipts.receipt_sharing.domain.filters

import kotlinx.serialization.Serializable

@Serializable
data class OrderingRequest(
    val ordering : RecipeOrdering,
    val ascending : Boolean
)
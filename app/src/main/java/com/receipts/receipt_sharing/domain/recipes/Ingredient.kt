package com.receipts.receipt_sharing.domain.recipes

import kotlinx.serialization.Serializable

@Serializable
data class Ingredient(
    var name : String,
    val amount : Float,
    val measureType : Measure
)
package com.receipts.receipt_sharing.domain.reviews

import kotlinx.serialization.Serializable

@Serializable
data class RecipeReview(
    val _id : String,
    val recipeID : String,
    val userID : String,
    val text : String,
    val rating : Int
)
package com.receipts.receipt_sharing.domain.reviews

data class RecipeReview(
    val _id : String,
    val userID : String,
    val recipeID : String,
    val text : String,
    val rating : Int
)

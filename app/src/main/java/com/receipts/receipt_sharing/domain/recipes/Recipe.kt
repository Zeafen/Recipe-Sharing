package com.receipts.receipt_sharing.domain.recipes

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val recipeID : String,
    val creatorID : String,
    val imageUrl : String?,
    val recipeName : String,
    val description : String?,
    val ingredients : List<Ingredient>,
    val steps : List<Step>,
    val currentRating : Float,
    val reviewsCount : Long,
    val viewsCount : Long
)
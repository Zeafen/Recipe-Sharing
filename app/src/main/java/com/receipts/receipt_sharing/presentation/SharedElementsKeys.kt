package com.receipts.receipt_sharing.presentation
enum class RecipeSharedElementType{
    Background,
    Bounds,
    Image,
    Title,
    Description,
}
data class RecipeSharedElementKey(
    val id : String = "default",
    val origin : String,
    val type : RecipeSharedElementType
)
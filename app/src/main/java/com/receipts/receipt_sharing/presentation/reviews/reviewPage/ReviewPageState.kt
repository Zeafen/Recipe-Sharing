package com.receipts.receipt_sharing.presentation.reviews.reviewPage

import com.receipts.receipt_sharing.domain.response.ApiResult
import com.receipts.receipt_sharing.domain.reviews.ReviewModel

data class ReviewPageState(
    val review : ApiResult<ReviewModel> = ApiResult.Downloading(),
    val isError : Boolean = true,
    val reviewText : String = "",
    val reviewRating : Int = 0,
    val selectedRecipeID : String = "",
    val recipeName : String = "",
    val recipeImageUrl : String = "",
    val userName : String = "",
    val userImageUrl : String = "",
    val infoMessage : String? = null
)
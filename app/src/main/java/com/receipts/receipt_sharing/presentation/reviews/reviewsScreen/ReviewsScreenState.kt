package com.receipts.receipt_sharing.presentation.reviews.reviewsScreen

import com.receipts.receipt_sharing.domain.response.ApiResult
import com.receipts.receipt_sharing.domain.reviews.ReviewModel
import com.receipts.receipt_sharing.presentation.PageSizes

data class ReviewsScreenState(
    val ownReview : ApiResult<ReviewModel> = ApiResult.Downloading(),
    val reviews : ApiResult<List<ReviewModel>> = ApiResult.Downloading(),
    val currentPage : Int = 1,
    val pageSize : PageSizes = PageSizes.Standard,
    val totalPages : Int = 1,
    val selectedReview :ReviewModel? = null,
    val selectedRecipeID : String = "",
    val recipeName : String = "",
    val recipeImageUrl : String = "",
    val recipeRating : Float = 0f,
    val openConfirmDeleteDialog : Boolean = false,
    val selectedSorting: ReviewsSorting = ReviewsSorting.All,
    val openSortingBox : Boolean = false,
    val selectedOrdering : ReviewsOrdering = ReviewsOrdering.None,
    val openOrderingBox : Boolean = false,
    val isAscending : Boolean = true,
    val infoMessage : String = ""
)
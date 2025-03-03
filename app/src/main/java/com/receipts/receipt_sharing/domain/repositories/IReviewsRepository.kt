package com.receipts.receipt_sharing.domain.repositories

import com.receipts.receipt_sharing.domain.response.RecipeResult
import com.receipts.receipt_sharing.domain.reviews.OrderRequest
import com.receipts.receipt_sharing.domain.reviews.RecipeReview

interface IReviewsRepository {
    suspend fun getReviewsByRecipe(token: String, recipeID: String): RecipeResult<List<RecipeReview>>
    suspend fun getOrderedReviewsByRecipe(token: String, recipeID: String, request: OrderRequest): RecipeResult<List<RecipeReview>>

    suspend fun getOwnReviewByRecipe(token: String, recipeID: String): RecipeResult<RecipeReview>

    suspend fun getReviewsCountByRecipe(token: String, id: String): RecipeResult<Long>

    suspend fun getRecipeRating(token: String, id: String): RecipeResult<Double>

    suspend fun getPosReviewsByRecipe(token: String, id: String): RecipeResult<List<RecipeReview>>
    suspend fun getOrderedPosReviewsByRecipe(token: String, id: String, request: OrderRequest): RecipeResult<List<RecipeReview>>

    suspend fun getNegReviewsByRecipe(token: String, id: String): RecipeResult<List<RecipeReview>>
    suspend fun getOrderedNegReviewsByRecipe(token: String, id: String, request : OrderRequest): RecipeResult<List<RecipeReview>>

    suspend fun getReviewByID(token: String, id: String): RecipeResult<RecipeReview>

    suspend fun getIsOwnReview(token: String, id: String): RecipeResult<Boolean>

    suspend fun postReview(token: String, reviewRequest: RecipeReview): RecipeResult<Unit>

    suspend fun updateReview(token: String, reviewRequest: RecipeReview): RecipeResult<Unit>

    suspend fun deleteReview(token: String, id: String): RecipeResult<Unit>
}
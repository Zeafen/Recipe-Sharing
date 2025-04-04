package com.receipts.receipt_sharing.domain.repositories

import com.receipts.receipt_sharing.domain.response.PagedResult
import com.receipts.receipt_sharing.domain.response.ApiResult
import com.receipts.receipt_sharing.domain.reviews.OrderRequest
import com.receipts.receipt_sharing.domain.reviews.RecipeReview

interface ReviewsRepository {
    /**
     * Attempts to get recipe's reviews
     * @param token Authorization token
     * @param recipeID Recipe identifier to get reviews by
     * @param page Selected page
     * @param pageSize Items count per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if reviews have successfully been obtained
     */
    suspend fun getReviewsByRecipe(token: String, recipeID: String, page : Int, pageSize : Int): ApiResult<PagedResult<List<RecipeReview>>>

    /**
     * Attempts to get recipe's latest reviews
     * @param token Authorization token
     * @param recipeID Recipe identifier to get reviews by
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if reviews have successfully been obtained
     */
    suspend fun getTopReviewsByRecipe(token: String, recipeID: String): ApiResult<List<RecipeReview>>

    /**
     * Attempts to get recipe's ordered reviews
     * @param token Authorization token
     * @param recipeID Recipe identifier to get reviews by
     * @param request Order request body
     * @param page Selected page
     * @param pageSize Items count per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if reviews have successfully been obtained
     */
    suspend fun getOrderedReviewsByRecipe(token: String, recipeID: String, request: OrderRequest, page : Int, pageSize : Int): ApiResult<PagedResult<List<RecipeReview>>>

    /**
     * Attempts to get own recipe's review
     * @param token Authorization token
     * @param recipeID Recipe identifier to get reviews by
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if review has successfully been obtained
     */
    suspend fun getOwnReviewByRecipe(token: String, recipeID: String): ApiResult<RecipeReview>

    /**
     * Attempts to get recipe's reviews count
     * @param token Authorization token
     * @param id Recipe identifier to get reviews count by
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if count has successfully been obtained
     */
    suspend fun getReviewsCountByRecipe(token: String, id: String): ApiResult<Long>

    /**
     * Attempts to get recipe's average rating
     * @param token Authorization token
     * @param id Recipe identifier to to rating by
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if rating has successfully been obtained
     */
    suspend fun getRecipeRating(token: String, id: String): ApiResult<Double>

    /**
     * Attempts to get recipe's positive reviews
     * @param token Authorization token
     * @param id Recipe identifier to get reviews by
     * @param page Selected page
     * @param pageSize Items count per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if reviews have successfully been obtained
     */
    suspend fun getPosReviewsByRecipe(token: String, id: String, page : Int, pageSize : Int): ApiResult<PagedResult<List<RecipeReview>>>

    /**
     * Attempts to get recipe's ordered positive reviews
     * @param token Authorization token
     * @param id Recipe identifier to get reviews by
     * @param request Order request body
     * @param page Selected page
     * @param pageSize Items count per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if reviews have successfully been obtained
     */
    suspend fun getOrderedPosReviewsByRecipe(token: String, id: String, request: OrderRequest, page : Int, pageSize : Int): ApiResult<PagedResult<List<RecipeReview>>>

    /**
     * Attempts to get recipe's negative reviews
     * @param token Authorization token
     * @param id Recipe identifier to get reviews by
     * @param page Selected page
     * @param pageSize Items count per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if reviews have successfully been obtained
     */
    suspend fun getNegReviewsByRecipe(token: String, id: String, page : Int, pageSize : Int): ApiResult<PagedResult<List<RecipeReview>>>

    /**
     * Attempts to get recipe's ordered negative reviews
     * @param token Authorization token
     * @param id Recipe identifier to get reviews by
     * @param request Order request body
     * @param page Selected page
     * @param pageSize Items count per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if reviews have successfully been obtained
     */
    suspend fun getOrderedNegReviewsByRecipe(token: String, id: String, request : OrderRequest, page : Int, pageSize : Int): ApiResult<PagedResult<List<RecipeReview>>>

    /**
     * Attempts to get review by id
     * @param token Authorization token
     * @param id Review identifier to get review by
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if review has been successfully been obtained
     */
    suspend fun getReviewByID(token: String, id: String): ApiResult<RecipeReview>

    /**
     * Attempts to check if user owns review
     * @param token Authorization token
     * @param id Review identifier to check
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if request succeed
     */
    suspend fun getIsOwnReview(token: String, id: String): ApiResult<Boolean>

    /**
     * Attempts to post review
     * @param token Authorization token
     * @param reviewRequest Review to post
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if review has successfully been posted
     */
    suspend fun postReview(token: String, reviewRequest: RecipeReview): ApiResult<Unit>

    /**
     * Attempts to update review
     * @param token Authorization token
     * @param reviewRequest Review to update
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if review has successfully been updated
     */
    suspend fun updateReview(token: String, reviewRequest: RecipeReview): ApiResult<Unit>

    /**
     * Attempts to delete review
     * @param token Authorization token
     * @param id Review identifier to delete
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if review has successfully been deleted
     */
    suspend fun deleteReview(token: String, id: String): ApiResult<Unit>
}
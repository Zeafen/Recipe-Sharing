package com.receipts.receipt_sharing.data.repositoriesImpl

import com.receipts.receipt_sharing.domain.apiServices.RecipesAPIService
import com.receipts.receipt_sharing.domain.repositories.ReviewsRepository
import com.receipts.receipt_sharing.domain.response.ApiResult
import com.receipts.receipt_sharing.domain.response.PagedResult
import com.receipts.receipt_sharing.domain.reviews.OrderRequest
import com.receipts.receipt_sharing.domain.reviews.RecipeReview
import retrofit2.HttpException

class ReviewsRepositoryImpl(
    private val api: RecipesAPIService
) : ReviewsRepository {
    override suspend fun getReviewsByRecipe(
        token: String,
        recipeID: String,
        page : Int,
        pageSize : Int
    ): ApiResult<PagedResult<List<RecipeReview>>> {
        return try {
            ApiResult.Succeed(api.getReviewsByRecipe(token, recipeID, page, pageSize))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getTopReviewsByRecipe(
        token: String,
        recipeID: String
    ): ApiResult<List<RecipeReview>> {
        return try {
            ApiResult.Succeed(api.getTopReviewsByRecipe(token, recipeID))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getReviewsCountByRecipe(token: String, id: String): ApiResult<Long> {
        return try {
            ApiResult.Succeed(api.getReviewsCountByRecipe(token, id))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getRecipeRating(token: String, id: String): ApiResult<Double> {
        return try {
            ApiResult.Succeed(api.getRecipeRating(token, id))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getPosReviewsByRecipe(
        token: String,
        id: String,
        page : Int,
        pageSize : Int
    ): ApiResult<PagedResult<List<RecipeReview>>> {
        return try {
            ApiResult.Succeed(api.getPositiveReviewsByRecipe(token, id, page, pageSize))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getNegReviewsByRecipe(
        token: String,
        id: String,
        page : Int,
        pageSize : Int
    ): ApiResult<PagedResult<List<RecipeReview>>> {
        return try {
            ApiResult.Succeed(api.getNegativeReviewsByRecipe(token, id, page, pageSize))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getReviewByID(token: String, id: String): ApiResult<RecipeReview> {
        return try {
            ApiResult.Succeed(api.getReviewByID(token, id))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getIsOwnReview(token: String, id: String): ApiResult<Boolean> {
        return try {
            ApiResult.Succeed(api.getIsOwnReview(token, id))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun postReview(
        token: String,
        reviewRequest: RecipeReview
    ): ApiResult<Unit> {
        return try {
            api.postReview(token, reviewRequest)
            ApiResult.Succeed()
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun updateReview(
        token: String,
        reviewRequest: RecipeReview
    ): ApiResult<Unit> {
        return try {
            api.updateReview(token, reviewRequest)
            ApiResult.Succeed()
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun deleteReview(token: String, id: String): ApiResult<Unit> {
        return try {
            api.deleteReview(token, id)
            ApiResult.Succeed()
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getOrderedReviewsByRecipe(
        token: String,
        recipeID: String,
        request: OrderRequest,
        page : Int,
        pageSize : Int
    ): ApiResult<PagedResult<List<RecipeReview>>> {
        return try {
            ApiResult.Succeed(api.getOrderedReviewsByRecipe(token, recipeID, request, page, pageSize))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getOrderedPosReviewsByRecipe(
        token: String,
        id: String,
        request: OrderRequest,
        page : Int,
        pageSize : Int
    ): ApiResult<PagedResult<List<RecipeReview>>> {
        return try {
            ApiResult.Succeed(api.getOrderedPosReviewsByRecipe(token, id, request, page, pageSize))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getOrderedNegReviewsByRecipe(
        token: String,
        id: String,
        request: OrderRequest,
        page : Int,
        pageSize : Int
    ): ApiResult<PagedResult<List<RecipeReview>>> {
        return try {
            ApiResult.Succeed(api.getOrderedNegReviewsByRecipe(token, id, request, page, pageSize))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getOwnReviewByRecipe(
        token: String,
        recipeID: String
    ): ApiResult<RecipeReview> {
        return try {
            ApiResult.Succeed(api.getOwnReviewsByRecipe(token, recipeID))
        } catch (e: HttpException) {
            if(e.code() == 404)
                ApiResult.Succeed(null)
            else ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }
}
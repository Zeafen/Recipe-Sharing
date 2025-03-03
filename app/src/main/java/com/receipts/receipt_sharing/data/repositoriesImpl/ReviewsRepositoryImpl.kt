package com.receipts.receipt_sharing.data.repositoriesImpl

import com.receipts.receipt_sharing.domain.apiServices.RecipesAPIService
import com.receipts.receipt_sharing.domain.repositories.IReviewsRepository
import com.receipts.receipt_sharing.domain.response.RecipeResult
import com.receipts.receipt_sharing.domain.reviews.OrderRequest
import com.receipts.receipt_sharing.domain.reviews.RecipeReview
import retrofit2.HttpException

class ReviewsRepositoryImpl(
    private val api: RecipesAPIService
) : IReviewsRepository {
    override suspend fun getReviewsByRecipe(
        token: String,
        recipeID: String
    ): RecipeResult<List<RecipeReview>> {
        return try {
            RecipeResult.Succeed(api.getReviewsByRecipe(token, recipeID))
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getReviewsCountByRecipe(token: String, id: String): RecipeResult<Long> {
        return try {
            RecipeResult.Succeed(api.getReviewsCountByRecipe(token, id))
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getRecipeRating(token: String, id: String): RecipeResult<Double> {
        return try {
            RecipeResult.Succeed(api.getRecipeRating(token, id))
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getPosReviewsByRecipe(
        token: String,
        id: String
    ): RecipeResult<List<RecipeReview>> {
        return try {
            RecipeResult.Succeed(api.getPositiveReviewsByRecipe(token, id))
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getNegReviewsByRecipe(
        token: String,
        id: String
    ): RecipeResult<List<RecipeReview>> {
        return try {
            RecipeResult.Succeed(api.getNegativeReviewsByRecipe(token, id))
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getReviewByID(token: String, id: String): RecipeResult<RecipeReview> {
        return try {
            RecipeResult.Succeed(api.getReviewByID(token, id))
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getIsOwnReview(token: String, id: String): RecipeResult<Boolean> {
        return try {
            RecipeResult.Succeed(api.getIsOwnReview(token, id))
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun postReview(
        token: String,
        reviewRequest: RecipeReview
    ): RecipeResult<Unit> {
        return try {
            api.postReview(token, reviewRequest)
            RecipeResult.Succeed()
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun updateReview(
        token: String,
        reviewRequest: RecipeReview
    ): RecipeResult<Unit> {
        return try {
            api.updateReview(token, reviewRequest)
            RecipeResult.Succeed()
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun deleteReview(token: String, id: String): RecipeResult<Unit> {
        return try {
            api.deleteReview(token, id)
            RecipeResult.Succeed()
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getOrderedReviewsByRecipe(
        token: String,
        recipeID: String,
        request: OrderRequest
    ): RecipeResult<List<RecipeReview>> {
        return try {
            RecipeResult.Succeed(api.getOrderedReviewsByRecipe(token, recipeID, request))
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getOrderedPosReviewsByRecipe(
        token: String,
        id: String,
        request: OrderRequest
    ): RecipeResult<List<RecipeReview>> {
        return try {
            RecipeResult.Succeed(api.getOrderedPosReviewsByRecipe(token, id, request))
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getOrderedNegReviewsByRecipe(
        token: String,
        id: String,
        request: OrderRequest
    ): RecipeResult<List<RecipeReview>> {
        return try {
            RecipeResult.Succeed(api.getOrderedNegReviewsByRecipe(token, id, request))
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getOwnReviewByRecipe(
        token: String,
        recipeID: String
    ): RecipeResult<RecipeReview> {
        return try {
            RecipeResult.Succeed(api.getOwnReviewsByRecipe(token, recipeID))
        } catch (e: HttpException) {
            if(e.code() == 404)
                RecipeResult.Succeed(null)
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }
}
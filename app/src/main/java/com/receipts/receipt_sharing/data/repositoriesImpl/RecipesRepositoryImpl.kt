package com.receipts.receipt_sharing.data.repositoriesImpl

import com.receipts.receipt_sharing.domain.apiServices.RecipesAPIService
import com.receipts.receipt_sharing.domain.filters.RecipeFilteringRequest
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.repositories.RecipesRepository
import com.receipts.receipt_sharing.domain.response.ApiResult
import com.receipts.receipt_sharing.domain.response.PagedResult
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException


class RecipesRepositoryImpl(
    private val api: RecipesAPIService,
) : RecipesRepository {
    override suspend fun getTimeStats(token: String): ApiResult<Int> {
        return try {
            ApiResult.Succeed(
                api.getTimeStats(token)
            )
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun uploadRecipeImage(token: String, imageFile: File): ApiResult<String> {
        return try {
            ApiResult.Succeed(
                api.uploadRecipeImage(
                    token,
                    MultipartBody.Part
                        .createFormData(
                            "image",
                            imageFile.nameWithoutExtension,
                            imageFile.asRequestBody()
                        )
                )
            )
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun uploadCreatorImage(token: String, imageFile: File): ApiResult<String> {
        return try {
            ApiResult.Succeed(
                api.uploadCreatorImage(
                    token,
                    MultipartBody.Part
                        .createFormData(
                            "image",
                            imageFile.nameWithoutExtension,
                            imageFile.asRequestBody()
                        )
                )
            )
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }


    override suspend fun getRecipes(
        token: String,
        page: Int,
        pageSize: Int
    ): ApiResult<PagedResult<List<Recipe>>> {
        return try {
            ApiResult.Succeed(api.getRecipes(token, page, pageSize))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getOwnRecipes(
        token: String,
        page: Int,
        pageSize: Int
    ): ApiResult<PagedResult<List<Recipe>>> {
        return try {
            ApiResult.Succeed(api.getOwnRecipes(token, page, pageSize))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: IOException) {
            ApiResult.Error(e.message)
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getFilteredOwnRecipes(
        token: String,
        requested: RecipeFilteringRequest,
        page: Int,
        pageSize: Int
    ): ApiResult<PagedResult<List<Recipe>>> {
        return try {
            ApiResult.Succeed(api.getFilteredOwnRecipes(token, requested, page, pageSize))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: IOException) {
            ApiResult.Error(e.message)
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getOwnRecipesByName(
        token: String,
        name: String,
        page: Int,
        pageSize: Int
    ): ApiResult<PagedResult<List<Recipe>>> {
        return try {
            ApiResult.Succeed(api.getOwnRecipesByName(token, name, page, pageSize))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: IOException) {
            ApiResult.Error(e.message)
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getFilteredOwnRecipesByName(
        token: String,
        name: String,
        requested: RecipeFilteringRequest,
        page: Int,
        pageSize: Int
    ): ApiResult<PagedResult<List<Recipe>>> {
        return try {
            ApiResult.Succeed(
                api.getFilteredOwnRecipesByName(
                    token,
                    name,
                    requested,
                    page,
                    pageSize
                )
            )
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: IOException) {
            ApiResult.Error(e.message)
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error(e.message)
        }
    }

    override suspend fun isRecipeOwn(token: String, receiptID: String): ApiResult<Boolean> {
        return try {
            val data = api.getOwn(token, receiptID)
            ApiResult.Succeed(data)
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getRecipeByID(token: String, receiptID: String): ApiResult<Recipe> {
        return try {
            ApiResult.Succeed(api.getRecipeByID(token, receiptID))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getRecipesByCreator(
        token: String,
        creatorID: String,
        page: Int,
        pageSize: Int
    ): ApiResult<PagedResult<List<Recipe>>> {
        return try {
            ApiResult.Succeed(api.getRecipesByCreator(token, creatorID, page, pageSize))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getRecipesByCreatorByName(
        token: String,
        creatorID: String,
        recipeName: String,
        page: Int,
        pageSize: Int
    ): ApiResult<PagedResult<List<Recipe>>> {
        return try {
            ApiResult.Succeed(
                api.getRecipesByCreatorByName(
                    token,
                    creatorID,
                    recipeName,
                    page,
                    pageSize
                )
            )
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getFilteredRecipesByCreator(
        token: String,
        creatorID: String,
        request: RecipeFilteringRequest,
        page: Int,
        pageSize: Int
    ): ApiResult<PagedResult<List<Recipe>>> {
        return try {
            ApiResult.Succeed(
                api.getFilteredRecipesByCreator(
                    token,
                    creatorID,
                    page,
                    pageSize,
                    request
                )
            )
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getFilteredRecipesByCreatorByName(
        token: String,
        creatorID: String,
        recipeName: String,
        request: RecipeFilteringRequest,
        page: Int,
        pageSize: Int
    ): ApiResult<PagedResult<List<Recipe>>> {
        return try {
            ApiResult.Succeed(
                api.getFilteredRecipesByCreatorByName(
                    token,
                    creatorID,
                    recipeName,
                    request,
                    page,
                    pageSize
                )
            )
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getTopRecipesByCreator(
        token: String,
        creatorID: String,
        amount: Int
    ): ApiResult<List<Recipe>> {
        return try {
            ApiResult.Succeed(api.getTopRecipesByCreator(token, creatorID, amount))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getRecipesByName(
        token: String,
        name: String,
        page: Int,
        pageSize: Int
    ): ApiResult<PagedResult<List<Recipe>>> {
        return try {
            ApiResult.Succeed(api.getRecipesByName(token, name, page, pageSize))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getFavoritesByName(
        token: String,
        name: String,
        page: Int,
        pageSize: Int
    ): ApiResult<PagedResult<List<Recipe>>> {
        return try {
            ApiResult.Succeed(api.getFavoritesByName(token, name, page, pageSize))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getFilteredRecipes(
        token: String,
        requested: RecipeFilteringRequest,
        page: Int,
        pageSize: Int
    ): ApiResult<PagedResult<List<Recipe>>> {
        return try {
            ApiResult.Succeed(api.getFilteredRecipes(token, page, pageSize, requested))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getFilteredRecipesByName(
        token: String,
        requested: RecipeFilteringRequest,
        name: String,
        page: Int,
        pageSize: Int
    ): ApiResult<PagedResult<List<Recipe>>> {
        return try {
            ApiResult.Succeed(
                api.getFilteredRecipesByName(
                    token,
                    name,
                    page,
                    pageSize,
                    requested
                )
            )
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun postRecipe(token: String, request: Recipe): ApiResult<String> {
        return try {
            ApiResult.Succeed(api.postRecipe(token, request))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun deleteRecipe(token: String, receiptID: String): ApiResult<Unit> {
        return try {
            api.deleteRecipe(token, receiptID)
            ApiResult.Succeed()
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun updateRecipe(token: String, recipe: Recipe): ApiResult<Unit> {
        return try {
            api.updateRecipe(token, recipe)
            ApiResult.Succeed()
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getFavorites(
        token: String,
        page: Int,
        pageSize: Int
    ): ApiResult<PagedResult<List<Recipe>>> {
        return try {
            ApiResult.Succeed(api.getFavorites(token, page, pageSize))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getFilteredFavorites(
        token: String,
        filters: RecipeFilteringRequest,
        page: Int,
        pageSize: Int
    ): ApiResult<PagedResult<List<Recipe>>> {
        return try {
            ApiResult.Succeed(api.getFilteredFavorites(token, filters, page, pageSize))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getFilteredFavoritesByName(
        token: String,
        name: String,
        filters: RecipeFilteringRequest,
        page: Int,
        pageSize: Int
    ): ApiResult<PagedResult<List<Recipe>>> {
        return try {
            ApiResult.Succeed(
                api.getFilteredFavoritesByName(
                    token,
                    name,
                    page,
                    pageSize,
                    filters
                )
            )
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun isRecipeInFavorites(
        token: String,
        receiptID: String
    ): ApiResult<Boolean> {
        return try {
            ApiResult.Succeed(api.getIsFavorite(token, receiptID))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun addToFavorites(token: String, receiptID: String): ApiResult<Unit> {
        return try {
            api.addToFavorites(token, receiptID)
            ApiResult.Succeed()
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun removeFromFavorites(
        token: String,
        receiptID: String
    ): ApiResult<Unit> {
        return try {
            api.removeFromFavorites(token, receiptID)
            ApiResult.Succeed()
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }


}
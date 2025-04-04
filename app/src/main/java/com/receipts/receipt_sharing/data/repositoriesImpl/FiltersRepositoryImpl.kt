package com.receipts.receipt_sharing.data.repositoriesImpl

import com.receipts.receipt_sharing.domain.filters.FilterRequest
import com.receipts.receipt_sharing.domain.apiServices.RecipesAPIService
import com.receipts.receipt_sharing.domain.repositories.FiltersRepository
import com.receipts.receipt_sharing.domain.response.ApiResult
import retrofit2.HttpException

class FiltersRepositoryImpl(
    private val api : RecipesAPIService
) : FiltersRepository {
    override suspend fun getCategories(token: String): ApiResult<List<String>> {
        return try {
            ApiResult.Succeed(api.getCategories(token))
        } catch(e : HttpException){
            ApiResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getFiltersByCategory(
        token: String,
        category: String
    ): ApiResult<List<String>> {
        return try {
            ApiResult.Succeed(api.getFiltersByCategory(token, category))
        } catch(e : HttpException){
            ApiResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getCategorizedFilters(token: String): ApiResult<Map<String, List<String>>> {
        return try {
            ApiResult.Succeed(api.getCategorizedFilters(token))
        } catch(e : HttpException){
            ApiResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getFiltersByRecipe(token: String, id: String): ApiResult<List<String>> {
        return try {
            ApiResult.Succeed(api.getFiltersByRecipe(token, id))
        } catch(e : HttpException){
            ApiResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            ApiResult.Error(e.message)
        }
    }

    override suspend fun attachFiltersToRecipe(
        token: String,
        recipeID: String,
        filters: List<String>
    ): ApiResult<Unit> {
        return try {
            api.attachFiltersToRecipe(token, FilterRequest(recipeID, filters))
            ApiResult.Succeed()
        } catch(e : HttpException){
            ApiResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            ApiResult.Error(e.message)
        }
    }

    override suspend fun removeFiltersFromRecipe(
        token: String,
        recipeID: String,
        filters: List<String>
    ): ApiResult<Unit> {
        return try {
            api.removeFiltersFromRecipe(token, FilterRequest(recipeID, filters))
            ApiResult.Succeed()
        } catch(e : HttpException){
            ApiResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            ApiResult.Error(e.message)
        }
    }

    override suspend fun clearRecipeFilters(token: String, recipeID: String): ApiResult<Unit> {
        return try {
            api.clearRecipeFilters(token, recipeID)
            ApiResult.Succeed()
        } catch(e : HttpException){
            ApiResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            ApiResult.Error(e.message)
        }
    }
}
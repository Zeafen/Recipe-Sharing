package com.receipts.receipt_sharing.data.repositoriesImpl

import com.receipts.receipt_sharing.domain.apiServices.RecipesAPIService
import com.receipts.receipt_sharing.domain.filters.ApplyFiltersRequest
import com.receipts.receipt_sharing.domain.repositories.FiltersRepository
import com.receipts.receipt_sharing.domain.response.ApiResult
import retrofit2.HttpException

class FiltersRepositoryImpl(
    private val api : RecipesAPIService
) : FiltersRepository {
    override suspend fun getCategories(token: String, locale : String): ApiResult<List<String>> {
        return try {
            ApiResult.Succeed(api.getCategories(token, locale))
        } catch(e : HttpException){
            ApiResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getFiltersByCategory(
        token: String,
        category: String,
        locale : String
    ): ApiResult<List<String>> {
        return try {
            ApiResult.Succeed(api.getFiltersByCategory(token, category, locale))
        } catch(e : HttpException){
            ApiResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getCategorizedFilters(token: String, locale : String): ApiResult<Map<String, List<String>>> {
        return try {
            ApiResult.Succeed(api.getCategorizedFilters(token, locale))
        } catch(e : HttpException){
            ApiResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getFiltersByRecipe(token: String, id: String, locale : String): ApiResult<List<String>> {
        return try {
            ApiResult.Succeed(api.getFiltersByRecipe(token, id, locale))
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
            api.attachFiltersToRecipe(token, ApplyFiltersRequest(recipeID, filters))
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
            api.removeFiltersFromRecipe(token, ApplyFiltersRequest(recipeID, filters))
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
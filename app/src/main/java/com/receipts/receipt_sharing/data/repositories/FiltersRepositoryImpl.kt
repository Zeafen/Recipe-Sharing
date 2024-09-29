package com.receipts.receipt_sharing.data.repositories

import com.receipts.receipt_sharing.data.FilterRequest
import com.receipts.receipt_sharing.data.response.RecipeResult
import com.receipts.receipt_sharing.domain.apiServices.RecipesAPIService
import retrofit2.HttpException

class FiltersRepositoryImpl(
    private val api : RecipesAPIService
) : IFiltersRepository {
    override suspend fun GetCategories(token: String): RecipeResult<List<String>> {
        return try {
            RecipeResult.Succeed(api.getCategories(token))
        }
        catch (e : HttpException){
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getFiltersByCategory(
        token: String,
        category: String
    ): RecipeResult<List<String>> {
        return try {
            RecipeResult.Succeed(api.getFiltersByCategory(token, category))
        }
        catch (e : HttpException) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getCategorizedFilters(token: String): RecipeResult<Map<String, List<String>>> {
        return try {
            RecipeResult.Succeed(api.getCategorizedFilters(token))
        }
        catch (e : HttpException) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getFiltersByRecipe(token: String, id: String): RecipeResult<List<String>> {
        return try {
            RecipeResult.Succeed(api.getFiltersByRecipe(token, id))
        }
        catch (e : HttpException) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun attachFiltersToRecipe(
        token: String,
        recipeID: String,
        filters: List<String>
    ): RecipeResult<Unit> {
        return try {
            api.attachFiltersToRecipe(token, FilterRequest(recipeID, filters))
            RecipeResult.Succeed()
        }
        catch (e : HttpException) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun removeFiltersFromRecipe(
        token: String,
        recipeID: String,
        filters: List<String>
    ): RecipeResult<Unit> {
        return try {
            api.removeFiltersFromRecipe(token, FilterRequest(recipeID, filters))
            RecipeResult.Succeed()
        }
        catch (e : HttpException){
            RecipeResult.Error(e.message)
        } catch (e: Exception) {
            e.printStackTrace()
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun clearRecipeFilters(token: String, recipeID: String): RecipeResult<Unit> {
        return try {
            api.clearRecipeFilters(token, recipeID)
            RecipeResult.Succeed()
        }
        catch (e : HttpException){
            RecipeResult.Error(e.message)
        } catch (e: Exception) {
            e.printStackTrace()
            RecipeResult.Error(e.message)
        }
    }
}
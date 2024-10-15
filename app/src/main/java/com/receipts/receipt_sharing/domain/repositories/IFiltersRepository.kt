package com.receipts.receipt_sharing.domain.repositories

import com.receipts.receipt_sharing.domain.response.RecipeResult

interface IFiltersRepository {
    suspend fun GetCategories(
        token : String
    ) : RecipeResult<List<String>>

    suspend fun getFiltersByCategory(
        token : String,
        category : String
    ) : RecipeResult<List<String>>

    suspend fun getCategorizedFilters(
        token : String
    ) : RecipeResult<Map<String, List<String>>>

    suspend fun getFiltersByRecipe(
        token : String,
        id : String
    ) : RecipeResult<List<String>>

    suspend fun attachFiltersToRecipe(
        token : String,
        recipeID : String,
        filters : List<String>
    ) : RecipeResult<Unit>

    suspend fun removeFiltersFromRecipe(
        token : String,
        recipeID : String,
        filters : List<String>
    ) : RecipeResult<Unit>

    suspend fun clearRecipeFilters(
        token : String,
        recipeID : String,
    ) : RecipeResult<Unit>
}
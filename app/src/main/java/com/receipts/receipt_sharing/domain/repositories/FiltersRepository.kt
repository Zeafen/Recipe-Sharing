package com.receipts.receipt_sharing.domain.repositories

import com.receipts.receipt_sharing.domain.response.ApiResult
import java.util.Locale

interface FiltersRepository {

    /**
     * Attempts to get filters' categories
     * @param token Authorization token
     * @param locale Current Locale
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if categories have been obtained
     */
    suspend fun getCategories(
        token : String,
        locale : String = Locale.getDefault().language
    ) : ApiResult<List<String>>

    /**
     * Attempts to get filters by category
     * @param token Authorization token
     * @param category Category name to get filters by
     * @param locale current Locale
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if filters have been obtained
     */
    suspend fun getFiltersByCategory(
        token : String,
        category : String,
        locale : String = Locale.getDefault().language
    ) : ApiResult<List<String>>

    /**
     * Attempts to get filters grouped by categories
     * @param token Authorization token
     * @param locale current Locale
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if filters have been obtained
     */
    suspend fun getCategorizedFilters(
        token : String,
        locale : String = Locale.getDefault().language
    ) : ApiResult<Map<String, List<String>>>

    /**
     * Attempts to get recipe's filters
     * @param token Authorization token
     * @param locale current Locale
     * @param id Recipe identifier to get filters by
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if filters have been obtained
     */
    suspend fun getFiltersByRecipe(
        token : String,
        id : String,
        locale : String = Locale.getDefault().language
    ) : ApiResult<List<String>>

    /**
     * Attempts to attach filters to recipe
     * @param token Authorization token
     * @param recipeID Recipe identifier to attach filters to
     * @param filters filters to attach
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if filters have been attached
     */
    suspend fun attachFiltersToRecipe(
        token : String,
        recipeID : String,
        filters : List<String>
    ) : ApiResult<Unit>

    /**
     * Attempts to remove filters from recipe
     * @param token Authorization token
     * @param recipeID Recipe identifier to remove filters from
     * @param filters filters to remove
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if filters have been removed
     */
    suspend fun removeFiltersFromRecipe(
        token : String,
        recipeID : String,
        filters : List<String>
    ) : ApiResult<Unit>

    /**
     * Attempts to remove all filters from recipes
     * @param token Authorization token
     * @param recipeID Recipe identifier to remove filters from
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if filters have been removed
     */
    suspend fun clearRecipeFilters(
        token : String,
        recipeID : String,
    ) : ApiResult<Unit>
}
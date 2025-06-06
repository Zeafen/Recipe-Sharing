package com.receipts.receipt_sharing.domain.repositories

import com.receipts.receipt_sharing.domain.filters.RecipeFilteringRequest
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.response.ApiResult
import com.receipts.receipt_sharing.domain.response.PagedResult
import java.io.File

interface RecipesRepository {

    /**
     * @param token Authorization token
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] with the recipe max time
     */
    suspend fun getTimeStats(token : String) : ApiResult<Int>

    /**
     * Attempts to upload image to recipe
     * @param token Authorization token
     * @param imageFile Image file to attach
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if image have been successfully uploaded
     */
    suspend fun uploadRecipeImage(token: String, imageFile: File): ApiResult<String>

    /**
     * Attempts to upload image to user
     * @param token Authorization token
     * @param imageFile Image file to attach
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if image have been successfully uploaded
     */
    suspend fun uploadCreatorImage(token: String, imageFile: File): ApiResult<String>

    /**
     * Attempts to get recipes
     * @param token Authorization token
     * @param page Selected page
     * @param pageSize Items count per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if recipes have successfully been obtained
     */
    suspend fun getRecipes(token: String, page: Int, pageSize: Int): ApiResult<PagedResult<List<Recipe>>>

    /**
     * Attempts to get own recipes
     * @param token Authorization token
     * @param page Selected page
     * @param pageSize Items count per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if recipes have successfully been obtained
     */
    suspend fun getOwnRecipes(token: String, page: Int, pageSize: Int): ApiResult<PagedResult<List<Recipe>>>

    /**
     * Attempts to get own recipes, filtered by name
     * @param token Authorization token
     * @param page Selected page
     * @param pageSize Items count per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if recipes have successfully been obtained
     */
    suspend fun getOwnRecipesByName(token: String, name : String, page: Int, pageSize: Int): ApiResult<PagedResult<List<Recipe>>>

    /**
     * Attempts to get own filtered recipes
     * @param token Authorization token
     * @param page Selected page
     * @param pageSize Items count per page
     * @param requested Filter request body
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if recipes have successfully been obtained
     */
    suspend fun getFilteredOwnRecipes(token: String, requested : RecipeFilteringRequest, page: Int, pageSize: Int): ApiResult<PagedResult<List<Recipe>>>

    /**
     * Attempts to get own filtered recipes
     * @param token Authorization token
     * @param requested Filter request body
     * @param name Recipes' searched name
     * @param page Selected page
     * @param pageSize Items count per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if recipes have successfully been obtained
     */
    suspend fun getFilteredOwnRecipesByName(token: String, name : String, requested : RecipeFilteringRequest, page: Int, pageSize: Int): ApiResult<PagedResult<List<Recipe>>>

    /**
     * Attempts to get recipe by id
     * @param token Authorization token
     * @param receiptID Recipe identifier to get information by
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if recipe has successfully been obtained
     */
    suspend fun getRecipeByID(
        token: String,
        receiptID: String
    ): ApiResult<Recipe>

    /**
     * Attempts to get filtered recipes
     * @param token Authorization token
     * @param requested Filter request body
     * @param page Selected page
     * @param pageSize Items count per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if recipes have successfully been obtained
     */
    suspend fun getFilteredRecipes(
        token: String,
        requested: RecipeFilteringRequest,
        page: Int,
        pageSize: Int
    ): ApiResult<PagedResult<List<Recipe>>>

    /**
     * Attempts to get filtered recipes
     * @param token Authorization token
     * @param name Recipes' searched name
     * @param requested Filter request body
     * @param page Selected page
     * @param pageSize Items count per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if recipes have successfully been obtained
     */
    suspend fun getFilteredRecipesByName(
        token: String,
        requested: RecipeFilteringRequest,
        name: String,
        page : Int,
        pageSize : Int
    ): ApiResult<PagedResult<List<Recipe>>>

    /**
     * Attempts to get creator's recipes
     * @param token Authorization token
     * @param creatorID Creator identifier to get recipes by
     * @param page Selected page
     * @param pageSize Items count per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if recipes have successfully been obtained
     */
    suspend fun getRecipesByCreator(
        token: String,
        creatorID: String,
        page : Int,
        pageSize : Int
    ): ApiResult<PagedResult<List<Recipe>>>

    /**
     * Attempts to get most popular creator's recipes
     * @param token Authorization token
     * @param creatorID Creator identifier t get recipes by
     * @param amount Max amount of recipes to get
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if recipes have successfully been obtained
     */
    suspend fun getTopRecipesByCreator(
        token: String,
        creatorID: String,
        amount : Int = 10
    ): ApiResult<List<Recipe>>

    /**
     * Attempts to get creator's recipes, filtered by name
     * @param token Authorization token
     * @param creatorID Creator identifier to get recipes by
     * @param recipeName Recipes' searched name
     * @param page Selected page
     * @param pageSize Items count per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if recipes have successfully been obtained
     */
    suspend fun getRecipesByCreatorByName(
        token: String,
        creatorID: String,
        recipeName: String,
        page : Int,
        pageSize : Int
    ): ApiResult<PagedResult<List<Recipe>>>

    /**
     * Attempts to get creator's filtered recipes
     * @param token Authorization token
     * @param creatorID Creator identifier to get recipes by
     * @param recipeName Recipes' searched name
     * @param request Filter request body
     * @param page Selected page
     * @param pageSize Items count per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if recipes have successfully been obtained
     */
    suspend fun getFilteredRecipesByCreatorByName(
        token: String,
        creatorID: String,
        recipeName: String,
        request: RecipeFilteringRequest,
        page : Int,
        pageSize : Int
    ): ApiResult<PagedResult<List<Recipe>>>

    /**
     * Attempts to get creator's filtered recipes
     * @param token Authorization token
     * @param creatorID Creator identifier to get recipes by
     * @param request Filter request body
     * @param page Selected page
     * @param pageSize Items count per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if recipes have successfully been obtained
     */
    suspend fun getFilteredRecipesByCreator(
        token: String,
        creatorID: String,
        request: RecipeFilteringRequest,
        page : Int,
        pageSize : Int
    ): ApiResult<PagedResult<List<Recipe>>>

    /**
     * Attempts to get recipes, filtered by name
     * @param token Authorization token
     * @param name Recipes' searched name
     * @param page Selected page
     * @param pageSize Items count per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if recipes have successfully been obtained
     */
    suspend fun getRecipesByName(
        token: String,
        name: String,
        page : Int,
        pageSize : Int
    ): ApiResult<PagedResult<List<Recipe>>>

    /**
     * Attempts to get favorite recipes, filtered by name
     * @param token Authorization token
     * @param name Recipes' searched name
     * @param page Selected page
     * @param pageSize Items count per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if recipes have successfully been obtained
     */
    suspend fun getFavoritesByName(
        token: String,
        name: String,
        page : Int,
        pageSize : Int
    ): ApiResult<PagedResult<List<Recipe>>>

    /**
     * Attempts to get favorite filtered recipes
     * @param token Authorization token
     * @param filters Filter request body
     * @param page Selected page
     * @param pageSize Items count per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if recipes have successfully been obtained
     */
    suspend fun getFilteredFavorites(
        token: String,
        filters: RecipeFilteringRequest,
        page : Int,
        pageSize : Int
    ): ApiResult<PagedResult<List<Recipe>>>

    /**
     * Attempts to get favorite filtered recipes
     * @param token Authorization token
     * @param name Recipes' searched name
     * @param filters Filter request body
     * @param page Selected page
     * @param pageSize Items count per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if recipes have successfully been obtained
     */
    suspend fun getFilteredFavoritesByName(
        token: String,
        name: String,
        filters: RecipeFilteringRequest,
        page : Int,
        pageSize : Int
    ): ApiResult<PagedResult<List<Recipe>>>

    /**
     * Attempts to post recipe
     * @param token Authorization token
     * @param request Recipe to post
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if recipe has successfully been posted
     */
    suspend fun postRecipe(
        token: String,
        request: Recipe
    ): ApiResult<String>

    /**
     * Attempts to delete recipe
     * @param token Authorization token
     * @param receiptID Recipe identifier to delete
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if recipe has successfully been deleted
     */
    suspend fun deleteRecipe(
        token: String,
        receiptID: String
    ): ApiResult<Unit>

    /**
     * Attempts to update recipe
     * @param token Authorization token
     * @param recipe Recipe to update
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if recipe has successfully been updated
     */
    suspend fun updateRecipe(
        token: String,
        recipe: Recipe
    ): ApiResult<Unit>

    /**
     * Attempts to get favorite recipes
     * @param token Authorization token
     * @param page Selected page
     * @param pageSize Items count per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if recipes have successfully been obtained
     */
    suspend fun getFavorites(
        token: String,
        page : Int,
        pageSize : Int
    ): ApiResult<PagedResult<List<Recipe>>>

    /**
     * Attempts to check if recipe is in favorites
     * @param token Authorization token
     *  @param receiptID Recipe identifier to check
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if request succeed
     */
    suspend fun isRecipeInFavorites(
        token: String,
        receiptID: String
    ): ApiResult<Boolean>

    /**
     * Attempts to check if user owns recipe
     * @param token Authorization token
     *  @param receiptID Recipe identifier to check
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if request succeed
     */
    suspend fun isRecipeOwn(
        token: String,
        receiptID: String
    ): ApiResult<Boolean>

    /**
     * Attempts to add recipe to favorites
     * @param token Authorization token
     * @param receiptID Recipe identifier to add
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if recipe has successfully been added to favorites
     */
    suspend fun addToFavorites(
        token: String,
        receiptID: String
    ): ApiResult<Unit>

    /**
     * Attempts to remove recipe from favorites
     * @param token Authorization token
     * @param receiptID Recipe identifier to remove
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if recipe has successfully been removed from favorites
     */
    suspend fun removeFromFavorites(
        token: String,
        receiptID: String
    ): ApiResult<Unit>
}
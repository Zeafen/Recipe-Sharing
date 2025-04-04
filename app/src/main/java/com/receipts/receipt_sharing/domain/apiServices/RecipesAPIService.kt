package com.receipts.receipt_sharing.domain.apiServices

import com.receipts.receipt_sharing.domain.creators.ChangePasswRequest
import com.receipts.receipt_sharing.domain.creators.CreatorRequest
import com.receipts.receipt_sharing.domain.creators.EmailConfirmRequest
import com.receipts.receipt_sharing.domain.creators.ProfileRequest
import com.receipts.receipt_sharing.domain.filters.FilterRequest
import com.receipts.receipt_sharing.domain.filters.RecipeFilteringRequest
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.request.AuthRequest
import com.receipts.receipt_sharing.domain.response.PagedResult
import com.receipts.receipt_sharing.domain.reviews.OrderRequest
import com.receipts.receipt_sharing.domain.reviews.RecipeReview
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


interface RecipesAPIService {

    @POST("auth/signIn")
    suspend fun signIn(
        @Body request: AuthRequest
    ): String

    @POST("auth/signUp")
    suspend fun signUp(
        @Body request: AuthRequest
    )

    @GET("auth/authorize")
    suspend fun authorize(
        @Header("Authorization") token: String
    )

    @GET("auth/code")
    suspend fun sendCode(
        @Query("email") email: String
    )

    @POST("auth/password")
    suspend fun updatePassword(
        @Body request: ChangePasswRequest
    )

    @Multipart
    @POST("Files/recipes")
    suspend fun uploadRecipeImage(
        @Header("Authorization") token: String,
        @Part imagePart: MultipartBody.Part
    ): String

    @Multipart
    @POST("Files/creators")
    suspend fun uploadCreatorImage(
        @Header("Authorization") token: String,
        @Part imagePart: MultipartBody.Part
    ): String

    //Favorites and recipes

    @GET("Favorites")
    suspend fun getFavorites(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): PagedResult<List<Recipe>>

    @GET("Favorites/byname")
    suspend fun getFavoritesByName(
        @Header("Authorization") token: String,
        @Query("name") name: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): PagedResult<List<Recipe>>

    @POST("Favorites/filtered")
    suspend fun getFilteredFavorites(
        @Header("Authorization") token: String,
        @Body requested: RecipeFilteringRequest,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): PagedResult<List<Recipe>>

    @POST("Favorites/filtered/byname")
    suspend fun getFilteredFavoritesByName(
        @Header("Authorization") token: String,
        @Query("name") name: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Body requested: RecipeFilteringRequest
    ): PagedResult<List<Recipe>>

    @GET("Favorites/isinfavorite/{id}")
    suspend fun getIsFavorite(
        @Header("Authorization") token: String,
        @Path("id") receiptID: String
    ): Boolean

    @POST("Favorites")
    suspend fun addToFavorites(
        @Header("Authorization") token: String,
        @Query("id") id: String
    )

    @DELETE("Favorites/{id}")
    suspend fun removeFromFavorites(
        @Header("Authorization") token: String,
        @Path("id") id: String
    )

    @GET("Recipes/own/{id}")
    suspend fun getOwn(
        @Header("Authorization") token: String,
        @Path("id") recipeID: String
    ): Boolean

    @GET("Recipes/own")
    suspend fun getOwnRecipes(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): PagedResult<List<Recipe>>

    @GET("Recipes/own/byname")
    suspend fun getOwnRecipesByName(
        @Header("Authorization") token: String,
        @Query("name") name: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): PagedResult<List<Recipe>>

    @POST("Recipes/own/filtered")
    suspend fun getFilteredOwnRecipes(
        @Header("Authorization") token: String,
        @Body requested: RecipeFilteringRequest,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): PagedResult<List<Recipe>>

    @POST("Recipes/own/filtered/byname")
    suspend fun getFilteredOwnRecipesByName(
        @Header("Authorization") token: String,
        @Query("name") name: String,
        @Body requested: RecipeFilteringRequest,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): PagedResult<List<Recipe>>

    @POST("Recipes/filtered")
    suspend fun getFilteredRecipes(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Body requested: RecipeFilteringRequest
    ): PagedResult<List<Recipe>>

    @POST("Recipes/filtered/byname")
    suspend fun getFilteredRecipesByName(
        @Header("Authorization") token: String,
        @Query("name") name: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Body requested: RecipeFilteringRequest
    ): PagedResult<List<Recipe>>

    @GET("Recipes")
    suspend fun getRecipes(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): PagedResult<List<Recipe>>


    @GET("Recipes/{id}")
    suspend fun getRecipeByID(
        @Header("Authorization") token: String,
        @Path("id") receiptID: String
    ): Recipe

    @GET("Recipes/bycreator/{id}")
    suspend fun getRecipesByCreator(
        @Header("Authorization") token: String,
        @Path("id") creatorID: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): PagedResult<List<Recipe>>

    @GET("Recipes/bycreator/{id}/top")
    suspend fun getTopRecipesByCreator(
        @Header("Authorization") token: String,
        @Path("id") creatorID: String,
        @Query("amount") amount : Int
    ): List<Recipe>

    @GET("Recipes/bycreator/{id}/count")
    suspend fun getRecipesCountByCreator(
        @Header("Authorization") token: String,
        @Path("id") creatorID: String
    ): Long

    @GET("Recipes/bycreator/{id}/byname")
    suspend fun getRecipesByCreatorByName(
        @Header("Authorization") token: String,
        @Path("id") creatorID: String,
        @Query("name") recipeName: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): PagedResult<List<Recipe>>

    @POST("Recipes/bycreator/{id}/filtered")
    suspend fun getFilteredRecipesByCreator(
        @Header("Authorization") token: String,
        @Path("id") creatorID: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Body request: RecipeFilteringRequest
    ): PagedResult<List<Recipe>>

    @POST("Recipes/bycreator/{id}/filtered/byname")
    suspend fun getFilteredRecipesByCreatorByName(
        @Header("Authorization") token: String,
        @Path("id") creatorID: String,
        @Query("name") recipeName: String,
        @Body request: RecipeFilteringRequest,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): PagedResult<List<Recipe>>

    @GET("Recipes/byname")
    suspend fun getRecipesByName(
        @Header("Authorization") token: String,
        @Query("name") name: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): PagedResult<List<Recipe>>

    @POST("Recipes")
    suspend fun postRecipe(
        @Header("Authorization") token: String,
        @Body request: Recipe
    ): String

    @DELETE("Recipes/{id}")
    suspend fun deleteRecipe(
        @Header("Authorization") token: String,
        @Path("id") receiptID: String
    )

    @PUT("Recipes")
    suspend fun updateRecipe(
        @Header("Authorization") token: String,
        @Body receipt: Recipe
    )

    //Email

    @POST("Email/set")
    suspend fun setEmail(
        @Header("Authorization") token: String,
        @Query("email") email: String
    )

    @POST("Email/setcode")
    suspend fun setEmailGetCode(
        @Header("Authorization") token: String,
        @Query("email") email: String
    )

    @GET("Email/code")
    suspend fun getCode(
        @Header("Authorization") token: String
    )

    @POST("Email/confirm")
    suspend fun confirmEmail(
        @Header("Authorization") token: String,
        @Body request: EmailConfirmRequest
    )


    //Follows and creators
    @GET("Creators")
    suspend fun getCreators(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): PagedResult<List<CreatorRequest>>

    @GET("Creators/top")
    suspend fun getTopCreators(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): PagedResult<List<CreatorRequest>>

    @GET("Creators/{id}")
    suspend fun getCreatorById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): CreatorRequest

    @PUT("Creators")
    suspend fun updateCreator(
        @Header("Authorization") token: String,
        @Body request: ProfileRequest
    )

    @PUT("Creators/password")
    suspend fun updatePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswRequest
    )

    @GET("Creators/byname")
    suspend fun getCreatorsByName(
        @Header("Authorization") token: String,
        @Query("name") name: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): PagedResult<List<CreatorRequest>>

    @GET("Creators/self")
    suspend fun getUserInfo(
        @Header("Authorization") token: String
    ): ProfileRequest

    @GET("Follows")
    suspend fun getFollows(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): PagedResult<List<CreatorRequest>>

    @GET("Follows/byname")
    suspend fun getFollowsByName(
        @Header("Authorization") token: String,
        @Query("name") name: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): PagedResult<List<CreatorRequest>>

    @GET("Follows/count")
    suspend fun getFollowsCount(
        @Header("Authorization") token: String
    ): Long

    @GET("Follows/self")
    suspend fun getFollowers(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): PagedResult<List<CreatorRequest>>

    @GET("Follows/self/byname")
    suspend fun getFollowersByName(
        @Header("Authorization") token: String,
        @Query("name") name : String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): PagedResult<List<CreatorRequest>>

    @GET("Follows/self/count")
    suspend fun getFollowersCount(
        @Header("Authorization") token: String
    ): Long

    @GET("Follows/{id}")
    suspend fun getCreatorFollows(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): PagedResult<List<CreatorRequest>>

    @GET("Follows/{id}/byname")
    suspend fun getCreatorFollowsByName(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Query("name") name : String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): PagedResult<List<CreatorRequest>>

    @GET("Follows/{id}/count")
    suspend fun getCreatorFollowsCount(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Long

    @GET("Follows/bycreator/{id}")
    suspend fun getCreatorFollowers(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): PagedResult<List<CreatorRequest>>


    @GET("Follows/bycreator/{id}/byname")
    suspend fun getCreatorFollowersByName(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Query("name") name : String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): PagedResult<List<CreatorRequest>>

    @GET("Follows/bycreator/{id}/count")
    suspend fun getCreatorFollowersCount(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Long

    @POST("Follows")
    suspend fun addToFollows(
        @Header("Authorization") token: String,
        @Query("creatorId") creatorID: String
    )

    @DELETE("Follows/{id}")
    suspend fun removeFromFollows(
        @Header("Authorization") token: String,
        @Path("id") creatorID: String
    )

    @GET("Follows/isfollow/{id}")
    suspend fun doesFollow(
        @Header("Authorization") token: String,
        @Path("id") creatorID: String
    ): Boolean

    //Filters

    @GET("Filters/categories")
    suspend fun getCategories(
        @Header("Authorization") token: String
    ): List<String>

    @GET("Filters/by")
    suspend fun getFiltersByCategory(
        @Header("Authorization") token: String,
        @Query("categoryName") categoryName: String
    ): List<String>

    @GET("Filters/categorized")
    suspend fun getCategorizedFilters(
        @Header("Authorization") token: String
    ): Map<String, List<String>>

    @GET("Filters/byrecipe/{id}")
    suspend fun getFiltersByRecipe(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): List<String>

    @POST("Filters")
    suspend fun attachFiltersToRecipe(
        @Header("Authorization") token: String,
        @Body request: FilterRequest
    )

    @DELETE("Filters")
    suspend fun removeFiltersFromRecipe(
        @Header("Authorization") token: String,
        @Body request: FilterRequest
    )

    @DELETE("Filters/{id}")
    suspend fun clearRecipeFilters(
        @Header("Authorization") token: String,
        @Path("id") id: String
    )


    //Reviews

    @GET("Reviews/byrecipe/{id}")
    suspend fun getReviewsByRecipe(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): PagedResult<List<RecipeReview>>

    @GET("Reviews/byrecipe/{id}/top")
    suspend fun getTopReviewsByRecipe(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): List<RecipeReview>

    @POST("Reviews/byrecipe/{id}/ordered")
    suspend fun getOrderedReviewsByRecipe(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: OrderRequest,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): PagedResult<List<RecipeReview>>

    @GET("Reviews/byrecipe/{id}/own")
    suspend fun getOwnReviewsByRecipe(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): RecipeReview?


    @GET("Reviews/byrecipe/{id}/count")
    suspend fun getReviewsCountByRecipe(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Long

    @GET("Reviews/byrecipe/{id}/rating")
    suspend fun getRecipeRating(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Double

    @GET("Reviews/byrecipe/{id}/negative")
    suspend fun getPositiveReviewsByRecipe(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): PagedResult<List<RecipeReview>>

    @POST("Reviews/byrecipe/{id}/negative/ordered")
    suspend fun getOrderedNegReviewsByRecipe(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: OrderRequest,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): PagedResult<List<RecipeReview>>

    @GET("Reviews/byrecipe/{id}/positive")
    suspend fun getNegativeReviewsByRecipe(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): PagedResult<List<RecipeReview>>

    @POST("Reviews/byrecipe/{id}/positive/ordered")
    suspend fun getOrderedPosReviewsByRecipe(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: OrderRequest,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): PagedResult<List<RecipeReview>>

    @GET("Reviews/{id}")
    suspend fun getReviewByID(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): RecipeReview

    @GET("Reviews/isown/{id}")
    suspend fun getIsOwnReview(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Boolean

    @POST("Reviews")
    suspend fun postReview(
        @Header("Authorization") token: String,
        @Body request: RecipeReview
    )

    @PUT("Reviews")
    suspend fun updateReview(
        @Header("Authorization") token: String,
        @Body reviewRequest: RecipeReview
    )

    @DELETE("Reviews/{id}")
    suspend fun deleteReview(
        @Header("Authorization") token: String,
        @Path("id") id: String
    )

}
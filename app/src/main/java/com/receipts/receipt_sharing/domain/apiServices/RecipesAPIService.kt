package com.receipts.receipt_sharing.domain.apiServices

import com.receipts.receipt_sharing.data.CreatorRequest
import com.receipts.receipt_sharing.data.FilterRequest
import com.receipts.receipt_sharing.data.recipes.Recipe
import com.receipts.receipt_sharing.data.request.AuthRequest
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


interface RecipesAPIService {

    @POST("auth/signIn")
    suspend fun signIn(
        @Body request : AuthRequest
    ) : String
    @POST("auth/signUp")
    suspend fun signUp(
        @Body request : AuthRequest
    )
    @GET("auth/authorize")
    suspend fun authorize(
        @Header("Authorization") token : String
    )

    @POST("Files/recipes")
    suspend fun uploadRecipeImage(
        @Header("Authorization") token : String,
        @Part imagePart : MultipartBody.Part
    ) : String
    @POST("Files/creator")
    suspend fun uploadCreatorImage(
        @Header("Authorization") token : String,
        @Part imagePart : MultipartBody.Part
        ) : String

    //Favorites and recipes

    @GET("Favorites")
    suspend fun getFavorites(@Header("Authorization") token : String) : List<Recipe>

    @GET("Favorites/byname")
    suspend fun getFavoritesByName(@Header("Authorization") token : String, @Query("name") name : String) : List<Recipe>
    @GET("Favorites/filtered")
    suspend fun getFilteredFavorites(
        @Header("Authorization") token : String,
        @Body requested : List<String>
    ) : List<Recipe>
    @GET("Favorites/filtered/byname/{name}")
    suspend fun getFilteredFavoritesByName(
        @Header("Authorization") token : String,
        @Query("name") name : String,
        @Body requested : List<String>
    ) : List<Recipe>

    @GET("Favorites/{id}")
    suspend fun getIsFavorite(@Header("Authorization") token : String,
                              @Path("id") receiptID : String) : Boolean

    @GET("Recipes/own/{id}")
    suspend fun getOwn(@Header("Authorization") token : String,
                       @Path("id") recipeID : String) : Boolean

    @GET("Recipes/filtered")
    suspend fun getFilteredRecipes(@Header("Authorization")token : String,
                                   @Body requested : List<String>) : List<Recipe>
    @GET("Recipes/filtered/byname/{name}")
    suspend fun getFilteredRecipesByName(@Header("Authorization")token : String,
                                   @Path("name") name : String,
                                   @Body requested : List<String>
    ) : List<Recipe>

    @GET("Recipes")
    suspend fun getRecipes(@Header("Authorization") token : String) : List<Recipe>


    @GET("Recipes/{id}")
    suspend fun getRecipeByID(
        @Header("Authorization") token : String,
        @Path("id") receiptID : String
    ) : Recipe

    @GET("Recipes/bycreator/{id}")
    suspend fun getRecipesByCreator(
        @Header("Authorization") token : String,
        @Path("id") creatorID : String
    ) : List<Recipe>
    @GET("Recipes/bycreator")
    suspend fun getRecipesByCreatorByName(
        @Header("Authorization") token : String,
        @Query("id") creatorID : String,
        @Query("name") recipeName : String
    ) : List<Recipe>
    @GET("Recipes/bycreator/{id}/filtred")
    suspend fun getFilteredRecipesByCreator(
        @Header("Authorization") token : String,
        @Path("id") creatorID : String,
        @Body request : List<String>
    ) : List<Recipe>
    @GET("Recipes/bycreator/{id}/filtered/byname")
    suspend fun getFilteredRecipesByCreatorByName(
        @Header("Authorization") token : String,
        @Path("id") creatorID : String,
        @Query("name") recipeName : String,
        @Body request : List<String>
    ) : List<Recipe>

    @GET("Recipes/byname/{name}")
    suspend fun getRecipesByName(
        @Header("Authorization") token: String,
        @Path("name") name : String
    ) : List<Recipe>

    @POST("Recipes")
    suspend fun postRecipe(
        @Header("Authorization") token : String,
        @Body request : Recipe
    )

    @DELETE("Recipes/{id}")
    suspend fun deleteRecipe(
        @Header("Authorization") token : String,
        @Path("id") receiptID: String
    )

    @PUT("Recipes")
    suspend fun updateRecipe(
        @Header("Authorization") token : String,
        @Body receipt : Recipe
    )

    @POST("Favorites")
    suspend fun addToFavorites(
        @Header("Authorization") token : String,
        @Query("id") id : String
    )
    @DELETE("Favorites/{id}")
    suspend fun removeFromFavorites(
        @Header("Authorization") token : String,
        @Path("id") id : String
    )

    //Follows and creators
    @GET("Creators")
    suspend fun getCreators(
        @Header("Authorization") token : String,
    ) : List<CreatorRequest>

    @GET("Creators/{id}")
    suspend fun getCreatorById(
        @Header("Authorization") token : String,
        @Path("id") id : String
    ) : CreatorRequest

    @PUT("Creators")
    suspend fun updateCreator(
        @Header("Authorization") token : String,
        @Body request : CreatorRequest
    )

    @GET("Creators/byname")
    suspend fun getCreatorsByName(
        @Header("Authorization") token : String,
        @Query("name") name : String
    ) : List<CreatorRequest>

    @GET("Creators/self")
    suspend fun getUserInfo(
        @Header("Authorization") token : String
    ) : CreatorRequest

    @GET("Follows")
    suspend fun getFollows(
        @Header("Authorization") token : String
    ) : List<CreatorRequest>

    @GET("Follows/byname")
    suspend fun getFollowsByName(@Header("Authorization") token : String, @Body name : String) : List<CreatorRequest>

    @GET("Follows/self")
    suspend fun getFollowers(
        @Header("Authorization") token : String
    ) : List<CreatorRequest>

    @GET("Follows/self/{id}")
    suspend fun getCreatorFollowers(
        @Header("Authorization") token : String,
        @Path("id") id : String
    ) : List<CreatorRequest>

    @POST("Follows")
    suspend fun addToFollows(
        @Header("Authorization") token : String,
        @Query("creatorId") creatorID : String
        )

    @DELETE("Follows/{id}")
    suspend fun removeFromFollows(
        @Header("Authorization") token : String,
        @Path("id") creatorID : String
        )

    @GET("Follows/{id}")
    suspend fun doesFollow(
        @Header("Authorization") token : String,
        @Path("id") creatorID : String
        ) : Boolean

    @GET("Filters/categories")
    suspend fun getCategories(
        @Header("Authorization") token : String) : List<String>

    @GET("Filters/by")
    suspend fun getFiltersByCategory(
        @Header("Authorization") token : String,
        @Query("categoryName") categoryName : String) : List<String>

    @GET("Filters/categorized")
    suspend fun getCategorizedFilters(
        @Header("Authorization") token : String
    ) : Map<String, List<String>>

    @GET("Filters/byrecipe/{id}")
    suspend fun getFiltersByRecipe(
        @Header("Authorization") token : String,
        @Path("id") id : String) : List<String>

    @POST("Filters")
    suspend fun attachFiltersToRecipe(
        @Header("Authorization") token : String,
        @Body request : FilterRequest
    )

    @DELETE("Filters")
    suspend fun removeFiltersFromRecipe(
        @Header("Authorization") token : String,
        @Body request : FilterRequest
    )

    @DELETE("Filters/{id}")
    suspend fun clearRecipeFilters(
        @Header("Authorization") token : String,
        @Path("id") id : String
    )

}

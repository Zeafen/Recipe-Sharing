package com.receipts.receipt_sharing.data.repositoriesImpl

import IRecipesRepository
import com.receipts.receipt_sharing.domain.apiServices.RecipesAPIService
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.response.RecipeResult
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException


class RecipesRepositoryImpl (
    private val api : RecipesAPIService,
) : IRecipesRepository {

    override suspend fun uploadRecipeImage(token : String, imageFile : File): RecipeResult<String> {
        return try {
            RecipeResult.Succeed(api.uploadRecipeImage(token,
                MultipartBody.Part
                .createFormData("image", imageFile.nameWithoutExtension, imageFile.asRequestBody())))
        } catch(e : HttpException){
            RecipeResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun uploadCreatorImage(token: String, imageFile: File): RecipeResult<String> {
        return try {
            RecipeResult.Succeed(api.uploadCreatorImage(token,
                MultipartBody.Part
                    .createFormData("image", imageFile.nameWithoutExtension, imageFile.asRequestBody())))
        } catch(e : HttpException){
            RecipeResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            RecipeResult.Error(e.message)
        }
    }


    override suspend fun getRecipes(token: String): RecipeResult<List<Recipe>> {
        return try {
            RecipeResult.Succeed(api.getRecipes(token))
        } catch(e : HttpException){
            RecipeResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getOwnRecipes(token: String): RecipeResult<List<Recipe>> {
        return try {
            RecipeResult.Succeed(api.getOwnRecipes(token))
        } catch(e : HttpException){
            RecipeResult.Error(e.response()?.message()?:e.message())
        } catch (e : IOException){
            RecipeResult.Error(e.message)
        }
        catch (e : Exception){
            e.printStackTrace()
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun isRecipeOwn(token: String, receiptID: String): RecipeResult<Boolean> {
        return try {
            val data = api.getOwn(token, receiptID)
            RecipeResult.Succeed(data)
        } catch(e : HttpException){
            RecipeResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getRecipeByID(token: String, receiptID: String): RecipeResult<Recipe> {
        return try {
            RecipeResult.Succeed(api.getRecipeByID(token, receiptID))
        } catch(e : HttpException){
            RecipeResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getRecipesByCreator(
        token: String,
        creatorID: String
    ): RecipeResult<List<Recipe>> {
        return try {
            RecipeResult.Succeed(api.getRecipesByCreator(token, creatorID))
        } catch(e : HttpException){
            RecipeResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            RecipeResult.Error(e.message)
        }
    }
    override suspend fun getRecipesByCreatorByName(
        token: String,
        creatorID: String,
        recipeName: String
    ): RecipeResult<List<Recipe>> {
        return try {
            RecipeResult.Succeed(api.getRecipesByCreatorByName(token, creatorID, recipeName))
        } catch(e : HttpException){
            RecipeResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            RecipeResult.Error(e.message)
        }
    }
    override suspend fun getFilteredRecipesByCreator(
        token: String,
        creatorID: String,
        request : List<String>
    ): RecipeResult<List<Recipe>> {
        return try {
            RecipeResult.Succeed(api.getFilteredRecipesByCreator(token, creatorID, request))
        } catch(e : HttpException){
            RecipeResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            RecipeResult.Error(e.message)
        }
    }
    override suspend fun getFilteredRecipesByCreatorByName(
        token: String,
        creatorID: String,
        recipeName: String,
        request : List<String>
    ): RecipeResult<List<Recipe>> {
        return try {
            RecipeResult.Succeed(api.getFilteredRecipesByCreatorByName(token, creatorID, recipeName, request))
        } catch(e : HttpException){
            RecipeResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getRecipesByName(
        token: String,
        name: String
    ): RecipeResult<List<Recipe>> {
        return try {
            RecipeResult.Succeed(api.getRecipesByName(token, name))
        } catch(e : HttpException){
            RecipeResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getFavoritesByName(
        token: String,
        name: String
    ): RecipeResult<List<Recipe>> {
        return try {
            RecipeResult.Succeed(api.getFavoritesByName(token, name))
        } catch(e : HttpException){
            RecipeResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getFilteredRecipes(
        token: String,
        requested: List<String>
    ): RecipeResult<List<Recipe>> {
        return try {
            RecipeResult.Succeed(api.getFilteredRecipes(token, requested))
        } catch(e : HttpException){
            RecipeResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            RecipeResult.Error(e.message)
        }
    }
    override suspend fun getFilteredRecipesByName(
        token: String,
        requested: List<String>,
        name : String
    ): RecipeResult<List<Recipe>> {
        return try {
            RecipeResult.Succeed(api.getFilteredRecipesByName(token, name, requested))
        } catch(e : HttpException){
            RecipeResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun postRecipe(token: String, request: Recipe): RecipeResult<String> {
        return try {
            RecipeResult.Succeed(api.postRecipe(token, request))
        } catch(e : HttpException){
            RecipeResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun deleteRecipe(token: String, receiptID: String): RecipeResult<Unit> {
        return try {
            api.deleteRecipe(token,receiptID)
            RecipeResult.Succeed()
        } catch(e : HttpException){
            RecipeResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun updateRecipe(token: String, recipe: Recipe): RecipeResult<Unit> {
        return try {
            api.updateRecipe(token,recipe)
            RecipeResult.Succeed()
        } catch(e : HttpException){
            RecipeResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getFavorites(token: String): RecipeResult<List<Recipe>> {
        return try {
            RecipeResult.Succeed(api.getFavorites(token))
        } catch(e : HttpException){
            RecipeResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getFilteredFavorites(
        token: String,
        filters: List<String>
    ): RecipeResult<List<Recipe>> {
        return try {
            RecipeResult.Succeed(api.getFilteredFavorites(token, filters))
        } catch(e : HttpException){
            RecipeResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getFilteredFavoritesByName(
        token: String,
        name: String,
        filters: List<String>
    ): RecipeResult<List<Recipe>> {
        return try {
            RecipeResult.Succeed(api.getFilteredFavoritesByName(token, name, filters))
        } catch(e : HttpException){
            RecipeResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun isRecipeInFavorites(
        token: String,
        receiptID: String
    ): RecipeResult<Boolean> {
        return try {
            RecipeResult.Succeed(api.getIsFavorite(token, receiptID))
        } catch(e : HttpException){
            RecipeResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun addToFavorites(token: String, receiptID: String): RecipeResult<Unit> {
        return try {
            api.addToFavorites(token, receiptID)
            RecipeResult.Succeed()
        } catch(e : HttpException){
            RecipeResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun removeFromFavorites(
        token: String,
        receiptID: String
    ): RecipeResult<Unit> {
        return try {
            api.removeFromFavorites(token, receiptID)
            RecipeResult.Succeed()
        } catch(e : HttpException){
            RecipeResult.Error(e.response()?.message()?:e.message())
        } catch (e : Exception){
            RecipeResult.Error(e.message)
        }
    }
}
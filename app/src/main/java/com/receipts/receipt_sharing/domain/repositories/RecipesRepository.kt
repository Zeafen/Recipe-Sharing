
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.response.RecipeResult
import java.io.File

interface RecipesRepository {

    suspend fun uploadRecipeImage(token : String, imageFile : File) : RecipeResult<String>
    suspend fun uploadCreatorImage(token : String, imageFile : File) : RecipeResult<String>

    suspend fun getRecipes(token : String) : RecipeResult<List<Recipe>>

    suspend fun getRecipeByID(
        token : String,
        receiptID : String
    ) : RecipeResult<Recipe>

    suspend fun getFilteredRecipes(
        token : String,
        requested : List<String>
    ) : RecipeResult<List<Recipe>>
    suspend fun getFilteredRecipesByName(
        token : String,
        requested : List<String>,
        name : String
    ) : RecipeResult<List<Recipe>>

    suspend fun getRecipesByCreator(
        token : String,
        creatorID : String
    ) : RecipeResult<List<Recipe>>

    suspend fun getRecipesByCreatorByName(
        token : String,
        creatorID : String,
        recipeName : String
    ) : RecipeResult<List<Recipe>>

    suspend fun getFilteredRecipesByCreatorByName(
        token: String,
        creatorID: String,
        recipeName: String,
        request : List<String>
    ): RecipeResult<List<Recipe>>

    suspend fun getFilteredRecipesByCreator(
        token: String,
        creatorID: String,
        request : List<String>
    ): RecipeResult<List<Recipe>>

    suspend fun getRecipesByName(
        token: String,
        name : String
    ) : RecipeResult<List<Recipe>>

    suspend fun getFavoritesByName(
        token: String,
        name : String
    ) : RecipeResult<List<Recipe>>
    suspend fun getFilteredFavorites(
        token: String,
        filters : List<String>
    ) : RecipeResult<List<Recipe>>
    suspend fun getFilteredFavoritesByName(
        token: String,
        name : String,
        filters : List<String>
    ) : RecipeResult<List<Recipe>>

    suspend fun postRecipe(
        token : String,
        request : Recipe
    ) : RecipeResult<Unit>

    suspend fun deleteRecipe(
        token : String,
        receiptID: String
    ) : RecipeResult<Unit>

    suspend fun updateRecipe(
        token : String,
        recipe : Recipe
    ) : RecipeResult<Unit>

    suspend fun getFavorites(
        token : String
    ) : RecipeResult<List<Recipe>>

    suspend fun isRecipeInFavorites(
        token : String,
        receiptID : String
    ) : RecipeResult<Boolean>

    suspend fun isRecipeOwn(
        token : String,
        receiptID: String
    ) : RecipeResult<Boolean>

    suspend fun addToFavorites(
        token : String,
        receiptID : String
    ) : RecipeResult<Unit>

    suspend fun removeFromFavorites(
        token : String,
        receiptID : String
    ) : RecipeResult<Unit>
}
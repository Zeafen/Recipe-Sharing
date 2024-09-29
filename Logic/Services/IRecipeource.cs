using MongoDB.Bson;
using Recipes_API.Data.Recipes;

namespace Recipes_API.Domain.Services
{
    public interface IRecipesDataSource
    {
        public Task<List<Recipe>?> GetRecipes();
        public Task<Recipe?> GetRecipeByID(ObjectId recipeId);
        public Task<List<Recipe>?> GetRecipesByName(string name);
        public Task<List<Recipe>?> GetRecipesByCreator(ObjectId creatorId);
        public Task<bool> InsertRecipe(Recipe recipe);
        public Task<bool> UpdateRecipe(Recipe recipe);
        public Task<bool> DeleteRecipe(ObjectId recipeID);
        public Task<bool> GetOwnsRecipe(ObjectId recipeID, ObjectId userID);
    }
}

using Microsoft.AspNetCore.Http.HttpResults;
using MongoDB.Bson;
using MongoDB.Driver;
using Recipes_API.Data.Recipes;
using Recipes_API.Domain.Helpers;
using Recipes_API.Domain.Services;
using System.Net.WebSockets;

namespace Recipes_API.Domain.Implementations
{
    public class MongoRecipesDataSource : IRecipesDataSource
    {
        private readonly IMongoDatabase _database;
        private readonly IMongoCollection<Recipe> _recipes;

        public MongoRecipesDataSource(MongoClient client)
        {
            _database = client.GetDatabase(RecipesDatabaseInfo.Database_Name);
            _recipes = _database.GetCollection<Recipe>(RecipesDatabaseInfo.Recipes_Collection_Name);
        }

        public async Task<bool> DeleteRecipe(ObjectId recipeID)
        {
            try
            {
                var deleted = await _recipes.FindOneAndDeleteAsync(r => r._id.Equals(recipeID));
                if (deleted == null)
                    return false;
                return true;
            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<Recipe?> GetRecipeByID(ObjectId recipeId)
        {
            try
            {
                return await _recipes.Find(r => r._id.Equals(recipeId)).FirstOrDefaultAsync();
            }
            catch (Exception)
            {
                throw;
            }
        }


        public async Task<List<Recipe>?> GetRecipesByName(string name)
        {
            try
            {
                var list = await _recipes.Find("{}").ToListAsync();
                return (from r in list where r.recipeName.Contains(name) select r).ToList();
            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<List<Recipe>?> GetRecipes()
        {
            try
            {
                return await _recipes.Find("{}").ToListAsync();
            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<bool> InsertRecipe(Recipe recipe)
        {
            try
            {
                if (await _recipes.Find(r => r._id.Equals(recipe._id)).FirstOrDefaultAsync() != null)
                    return false;
                await _recipes.InsertOneAsync(recipe);
                return true;
            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<bool> UpdateRecipe(Recipe recipe)
        {
            try
            {
                var filter = Builders<Recipe>.Filter
                    .Eq(r => r._id, recipe._id);
                var update = Builders<Recipe>.Update
                    .Set(r => r.recipeName, recipe.recipeName)
                    .Set(r => r.description, recipe.description)
                    .Set(r => r.steps, recipe.steps)
                    .Set(r => r.ingredients, recipe.ingredients)
                    .Set(r => r.imageUrl, recipe.imageUrl);
                var updated = await _recipes.UpdateOneAsync(filter, update);
                if (updated == null || !updated.IsAcknowledged)
                    return false;
                return true;

            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<List<Recipe>?> GetRecipesByCreator(ObjectId creatorId)
        {
            try
            {
                return await _recipes.Find(r => r.creatorID.Equals(creatorId)).ToListAsync();
            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<bool> GetOwnsRecipe(ObjectId recipeID, ObjectId userID)
        {
            try
            {
                var list = await _recipes.Find("{}").ToListAsync();
                var searched = list.Where(r => r._id.Equals(recipeID)).FirstOrDefault();
                if(searched != null && searched.creatorID.Equals(userID))
                    return true;
                return false;
            }
            catch (Exception)
            {
                throw;
            }
        }
    }
}

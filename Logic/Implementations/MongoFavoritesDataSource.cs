using MongoDB.Bson;
using MongoDB.Driver;
using Recipes_API.Data.Recipes;
using Recipes_API.Data.User;
using Recipes_API.Domain.Helpers;
using Recipes_API.Logic.Services;
using System.Xml.Linq;

namespace Recipes_API.Logic.Implementations
{
    public class MongoFavoritesDataSource : IFavoritesDataSource
    {
        private readonly IMongoCollection<FavoriteRecord> _favorites;
        private readonly IMongoCollection<Recipe> _recipes;

        public MongoFavoritesDataSource(IMongoClient client)
        {
            _favorites = client
                .GetDatabase(RecipesDatabaseInfo.Database_Name)
                .GetCollection<FavoriteRecord>(RecipesDatabaseInfo.Favorites_Collection_Name);
            _recipes = client
                .GetDatabase(RecipesDatabaseInfo.Database_Name)
                .GetCollection<Recipe>(RecipesDatabaseInfo.Recipes_Collection_Name);
        }

        public async Task<bool> AddToFavorites(FavoriteRecord favorite)
        {
            try
            {
                var favorites = await _favorites.Find("{}").ToListAsync();
                var existing = favorites.Where(f => f.userID.Equals(favorite.userID) && f.recipeID.Equals(favorite.recipeID)).FirstOrDefault();
                if (existing != null)
                    return false;
                await _favorites.InsertOneAsync(favorite);
                return true;
            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<List<FavoriteRecord>?> GetFavorites(ObjectId userID)
        {
            try
            {
                var favorites = await _favorites.Find("{}").ToListAsync();
                return favorites.Where(f => f.userID.Equals(userID)).ToList();
            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<List<FavoriteRecord>> GetFavoritesByName(ObjectId userID, string recipeName)
        {
            try
            {
                var favorites = await _favorites.Find("{}").ToListAsync();
                favorites = favorites.Where(f => f.userID.Equals(userID)).ToList();
                List<FavoriteRecord> result = new List<FavoriteRecord>();
                foreach (var favorite in favorites)
                {
                    var recipe = await _recipes.Find(r => r._id.Equals(favorite.recipeID)).FirstOrDefaultAsync();
                    if (recipe != null && recipe.recipeName.Contains(recipeName, StringComparison.Ordinal))
                        result.Add(favorite);
                }
                return result;
            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<bool> IsFavorite(ObjectId userID, ObjectId recipeId)
        {
            try
            {
                var favorites = await _favorites.Find("{}").ToListAsync();
                var existing = favorites.FirstOrDefault(f => f.userID.Equals(userID) && f.recipeID.Equals(recipeId));
                return existing != null;
            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<bool> RemoveFromFavorites(ObjectId userID, ObjectId recipeID)
        {
            try
            {
                return (await _favorites.DeleteOneAsync(f => f.userID.Equals(userID) && f.recipeID.Equals(recipeID))).IsAcknowledged;
            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<bool> RemoveFromFavorites(ObjectId recordID)
        {
            try
            {
                return (await _favorites.DeleteOneAsync(f => f._id.Equals(recordID))).IsAcknowledged;
            }
            catch (Exception)
            {
                throw;
            }
        }
    }
}

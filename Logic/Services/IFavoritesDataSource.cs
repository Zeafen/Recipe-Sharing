using MongoDB.Bson;
using Recipes_API.Data.Recipes;

namespace Recipes_API.Logic.Services
{
    public interface IFavoritesDataSource
    {
        public Task<List<FavoriteRecord>> GetFavorites(ObjectId userID);
        public Task<List<FavoriteRecord>> GetFavoritesByName(ObjectId userID, string recipeName);
        public Task<bool> IsFavorite(ObjectId userID, ObjectId recipeId);
        public Task<bool> AddToFavorites(FavoriteRecord favorite);
        public Task<bool> RemoveFromFavorites(ObjectId userID, ObjectId recipeID);
        public Task<bool> RemoveFromFavorites(ObjectId recordID);
    }
}

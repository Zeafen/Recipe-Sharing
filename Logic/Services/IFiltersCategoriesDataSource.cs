using MongoDB.Bson;
using Receipts_API.Data.Filtering;

namespace Receipts_API.Logic.Services
{
    public interface IFiltersCategoriesDataSource
    {
        public Task<List<Category>> GetCategories();
        public Task<List<Filter>> GetFiltersByCategory(ObjectId categoryID);
        public Task<Dictionary<string, List<string>>> GetCategorizedFilters();
        public Task<List<Filter>> GetFiltersByRecipe(ObjectId recipeID);
        public Task<bool> AttachFilterToRecipe(ObjectId recipeID, ObjectId filterId);
        public Task<bool> AttachFilterToRecipe(ObjectId recipeID, string filterValue);
        public Task<bool> RemoveFilterFromRecipe(ObjectId recipeID, ObjectId filterId);
        public Task<bool> RemoveFilterFromRecipe(ObjectId recipeID, string filterValue);
        public Task<bool> ClearRecipeFilters(ObjectId recipeID);
    }
}

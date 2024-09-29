using Amazon.Runtime.Internal;
using MongoDB.Bson;
using MongoDB.Driver;
using Receipts_API.Data.Filtering;
using Receipts_API.Logic.Services;
using Recipes_API.Data.Recipes;
using Recipes_API.Domain.Helpers;

namespace Receipts_API.Logic.Implementations
{
    public class MongoFiltersCategoriesDataSource : IFiltersCategoriesDataSource
    {
        private IMongoCollection<Filter> _filters;
        private IMongoCollection<Category> _categories;
        private IMongoCollection<RecipeFilters> _recipeFilters;
        private IMongoCollection<Recipe> _recipes;

        public MongoFiltersCategoriesDataSource(MongoClient client)
        {
            var db = client.GetDatabase(RecipesDatabaseInfo.Database_Name);
            _filters = db.GetCollection<Filter>(RecipesDatabaseInfo.Filters_Collection_Name);
            _categories = db.GetCollection<Category>(RecipesDatabaseInfo.Categories_Collection_Name);
            _recipeFilters = db.GetCollection<RecipeFilters>(RecipesDatabaseInfo.RecipeFilters_Collection_Name);
            _recipes = db.GetCollection<Recipe>(RecipesDatabaseInfo.Recipes_Collection_Name);
        }

        public async Task<bool> AttachFilterToRecipe(ObjectId recipeID, ObjectId filterId)
        {
            try
            {
                var filter = (await _filters.Find("{}").ToListAsync()).FirstOrDefault(f => f._id.Equals(filterId));
                var recipe = (await _recipes.Find("{}").ToListAsync()).FirstOrDefault(f => f._id.Equals(recipeID));
                if (recipe is null || filter is null)
                    throw new Exception("Cannot find record with such id");
                var existed = await _recipeFilters.Find(rf => rf.recipeID.Equals(recipeID) && rf.filterID.Equals(filterId)).FirstOrDefaultAsync();
                var hasOne = (await GetFiltersByCategory(filter.categoryID)).Any(f => f._id.Equals(filterId));
                if (existed is null && !hasOne)
                {
                    _recipeFilters.InsertOne(new RecipeFilters(recipeID, filterId));
                    return true;
                }
                return false;
            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<bool> AttachFilterToRecipe(ObjectId recipeID, string filterValue)
        {
            try
            {
                var filters = await _filters.Find("{}").ToListAsync();
                var filter = filters.FirstOrDefault(f => f.filterValue == filterValue);
                if (filter is null)
                    return false;
                return await AttachFilterToRecipe(recipeID, filter._id);
            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<bool> ClearRecipeFilters(ObjectId recipeID)
        {
            try
            {
                if((await _recipeFilters.Find("{}").ToListAsync()).Count == 0)
                    return false;
                var filter = Builders<RecipeFilters>.Filter
                    .Eq(rf => rf.recipeID, recipeID);
                return (await _recipeFilters.DeleteManyAsync(filter)).IsAcknowledged;
            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<List<Category>> GetCategories()
        {
            try
            {
                return await _categories.Find("{}").ToListAsync();
            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<Dictionary<string, List<string>>> GetCategorizedFilters()
        {
            try
            {
                Dictionary<string, List<string>> result = new Dictionary<string, List<string>>();
                var categories = await GetCategories();
                foreach (var category in categories)
                {
                    var filters = from f in await GetFiltersByCategory(category._id) 
                                  select f.filterValue;
                    result.Add(category.categoryName, filters.ToList());
                }
                return result;
            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<List<Filter>> GetFiltersByCategory(ObjectId categoryID)
        {
            try
            {
                var filter = Builders<Filter>.Filter
                    .Eq(f => f.categoryID, categoryID);
                return await _filters.Find(filter).ToListAsync();
            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<List<Filter>> GetFiltersByRecipe(ObjectId recipeID)
        {
            try
            {
                var filter = Builders<RecipeFilters>.Filter
                    .Eq(f => f.recipeID, recipeID);
                var recipeFilters = await _recipeFilters.Find(filter).ToListAsync();
                List<Filter> result = new List<Filter>();
                foreach (var item in recipeFilters)
                {
                    var tag = await _filters.Find(f => f._id.Equals(item.filterID)).FirstOrDefaultAsync();
                    if(tag != null)
                        result.Add(tag);
                }
                return result;
            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<bool> RemoveFilterFromRecipe(ObjectId recipeID, ObjectId filterId)
        {
            try
            {
                return (await _recipeFilters.FindOneAndDeleteAsync(rf => rf.recipeID.Equals(recipeID) && rf.filterID.Equals(filterId))) != null;
            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<bool> RemoveFilterFromRecipe(ObjectId recipeID, string filterValue)
        {
            try
            {
                var filter = await _filters.Find(f => f.filterValue.Equals(filterValue, StringComparison.OrdinalIgnoreCase)).FirstOrDefaultAsync();
                if (filter is null)
                    throw new Exception("Filter with that value does not exist");
                return await RemoveFilterFromRecipe(recipeID, filter._id);
            }
            catch (Exception)
            {
                throw;
            }
        }
    }
}

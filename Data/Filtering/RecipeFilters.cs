using MongoDB.Bson;

namespace Receipts_API.Data.Filtering
{
    public class RecipeFilters
    {
        public ObjectId _id { get; set; } = ObjectId.GenerateNewId();
        public ObjectId recipeID { get; set; }
        public ObjectId filterID { get; set; }

        public RecipeFilters(ObjectId recipeID, ObjectId filterID)
        {
            this.recipeID = recipeID;
            this.filterID = filterID;
        }
    }
}

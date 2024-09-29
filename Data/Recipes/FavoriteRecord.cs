using MongoDB.Bson;

namespace Recipes_API.Data.Recipes
{
    public class FavoriteRecord
    {
        public ObjectId _id { get; set; } = ObjectId.GenerateNewId();
        public ObjectId userID { get; set; }
        public ObjectId recipeID { get; set; }

        public FavoriteRecord(ObjectId userID, ObjectId recipeID)
        {
            this.userID = userID;
            this.recipeID = recipeID;
        }
    }
}

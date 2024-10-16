using MongoDB.Bson;

namespace Receipts_API.Data.Filtering
{
    public class Category
    {
        public ObjectId _id { get; set; } = ObjectId.GenerateNewId();
        public string categoryName { get; set; }

        public Category(string categoryName)
        {
            this.categoryName = categoryName;
        }
        public Category() { }
    }
}

using MongoDB.Bson;

namespace Receipts_API.Data.Filtering
{
    public class Filter
    {
        public ObjectId _id { get; set; } = ObjectId.GenerateNewId();
        public ObjectId categoryID { get; set; }
        public string filterValue { get; set; }
        
        public Filter(ObjectId categoryID, string tagValue)
        {
            this.categoryID = categoryID;
            this.filterValue = tagValue;
        }
        public Filter() { }
    }
}

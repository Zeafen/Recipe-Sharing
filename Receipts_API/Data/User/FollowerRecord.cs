using MongoDB.Bson;

namespace Recipes_API.Data.User
{
    public class FollowerRecord
    {
        public ObjectId _id { get; set; } = ObjectId.GenerateNewId();
        public ObjectId userID { get; set; }
        public ObjectId creatorID { get; set; }

        public FollowerRecord(ObjectId userID, ObjectId creatorID)
        {
            this.userID = userID;
            this.creatorID = creatorID;
        }
        public FollowerRecord() { }
    }
}

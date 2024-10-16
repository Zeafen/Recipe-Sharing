using MongoDB.Bson;
using Recipes_API.Data.Recipes;
using Recipes_API.Data.User;

namespace Recipes_API.Logic.Services
{
    public interface IFollowersDataSource
    {
        public Task<List<FollowerRecord>> GetFollows(ObjectId userID);
        public Task<List<FollowerRecord>> GetFollowers(ObjectId creatorID);
        public Task<bool> Follows(ObjectId userID, ObjectId creatorID);
        public Task<bool> AddToFollows(FollowerRecord follower);
        public Task<bool> RemoveFromFollows(ObjectId userID, ObjectId creatorID);
        public Task<bool> RemoveFromFollows(ObjectId recordID);
    }
}

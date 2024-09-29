using Microsoft.IdentityModel.Tokens;
using MongoDB.Bson;
using MongoDB.Driver;
using Recipes_API.Data.Recipes;
using Recipes_API.Data.User;
using Recipes_API.Domain.Helpers;
using Recipes_API.Logic.Services;

namespace Recipes_API.Logic.Implementations
{
    public class MongoFollowersDataSource : IFollowersDataSource
    {
        private readonly IMongoCollection<FollowerRecord> _follows;

        public MongoFollowersDataSource(IMongoClient client)
        {

            _follows = client.GetDatabase(RecipesDatabaseInfo.Database_Name).GetCollection<FollowerRecord>(RecipesDatabaseInfo.Follows_Collection_Name);
        }


        public async Task<bool> AddToFollows(FollowerRecord follower)
        {
            try
            {
                var list = await _follows.Find("{}").ToListAsync();
                if (!list.IsNullOrEmpty() && list.FirstOrDefault(f => f.creatorID.Equals(follower.creatorID) && f.userID.Equals(follower.userID)) != null)
                    return false;
                await _follows.InsertOneAsync(follower);
                return true;
            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<bool> Follows(ObjectId userID, ObjectId creatorID)
        {
            try
            {
                var follows = await _follows.Find("{}").ToListAsync();
                return follows.FirstOrDefault(f => f.userID.Equals(userID) && f.creatorID.Equals(creatorID)) != null;
            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<List<FollowerRecord>> GetFollowers(ObjectId creatorID)
        {
            try
            {
                var list = await _follows.Find("{}").ToListAsync();
                return list.OrderBy(f => f.creatorID.Equals(creatorID)).ToList();
            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<List<FollowerRecord>> GetFollows(ObjectId userID)
        {
            try
            {
                var list = await _follows.Find("{}").ToListAsync();
                return list.OrderBy(f => f.userID.Equals(userID)).ToList();
            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<bool> RemoveFromFollows(ObjectId userID, ObjectId creatorID)
        {
            try
            {
                return (await _follows.DeleteOneAsync(f => f.userID.Equals(userID) && f.creatorID.Equals(creatorID))).IsAcknowledged;
            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<bool> RemoveFromFollows(ObjectId recordID)
        {
            try
            {
                return (await _follows.DeleteOneAsync(f => f._id.Equals(recordID))).IsAcknowledged;
            }
            catch (Exception)
            {
                throw;
            }
        }
    }
}

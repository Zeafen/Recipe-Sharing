using MongoDB.Bson;
using MongoDB.Driver;
using Recipes_API.Data.User;
using Recipes_API.Domain.Helpers;
using Recipes_API.Domain.Services;
using ZstdSharp.Unsafe;

namespace Recipes_API.Domain.Implementations
{
    public class MongoUserDataSource : IUserDataSource
    {
        private IMongoDatabase _db;
        private IMongoCollection<User> _users;

        public MongoUserDataSource(MongoClient client)
        {
            _db = client.GetDatabase(RecipesDatabaseInfo.Database_Name);
            _users = _db.GetCollection<User>(RecipesDatabaseInfo.Users_Collection_Name);
        }

        public async Task<List<CreatorRequest>> GetCreators()
        {
            try
            {
                var users = await _users.Find("{}").ToListAsync();
                return (from user in users
                        select (CreatorRequest)user).ToList();
            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<List<CreatorRequest>> GetCreatorsByName(string nickname)
        {
            try
            {
                var users = await _users.Find(u => u.nickname == nickname).ToListAsync();
                return (from user in users
                        select (CreatorRequest)user).ToList();
            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<User> GetUserByID(ObjectId userID)
        {
            try
            {
                return await _users.Find(u => u._id.Equals(userID)).FirstOrDefaultAsync();
            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<User?> GetUserByName(string username)
        {
            try
            {
                return await _users.Find(u => u.login == username).FirstOrDefaultAsync();
            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<bool> InsertUser(User user)
        {
            try
            {
                if ((await GetUserByName(user.login)) != null)
                    return false;
                await _users.InsertOneAsync(user);
                return true;
            }
            catch (Exception)
            {
                throw;
            }
        }

        public async Task<bool> UpdateUser(CreatorRequest request)
        {
            try
            {
                if(ObjectId.TryParse(request.userID, out var userId))
                {
                    var filter = Builders<User>.Filter.Eq(u => u._id, userId);
                    var update = Builders<User>.Update
                        .Set(u => u.nickname, request.nickname)
                        .Set(u => u.imageUrl, request.imageUrl);
                    return (await _users.UpdateOneAsync(filter, update)).IsAcknowledged;   
                }
                return false;
            }
            catch (Exception)
            {
                throw;
            }
        }
    }
}

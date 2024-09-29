using MongoDB.Bson;
using Recipes_API.Data.User;

namespace Recipes_API.Domain.Services
{
    public interface IUserDataSource
    {
        public Task<User> GetUserByID(ObjectId userID);
        public Task<User?> GetUserByName(string username);
        public Task<bool> InsertUser(User user);
        public Task<bool> UpdateUser(CreatorRequest request);
        public Task<List<CreatorRequest>> GetCreatorsByName(string nickname);
        public Task<List<CreatorRequest>> GetCreators();
    }
}

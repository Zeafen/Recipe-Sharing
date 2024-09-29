using Microsoft.AspNetCore.Routing.Matching;
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Bson.Serialization.IdGenerators;

namespace Recipes_API.Data.User
{
    public class User
    {
        public ObjectId _id { get; set; } = ObjectId.GenerateNewId();
        public string login { get; set; }
        public string nickname { get; set; } = $"User{DateTime.Now.Ticks}";
        public string imageUrl { get; set; } = string.Empty;
        public string password { get; set; }
        public string salt { get; set; }

        public User(string login, string password, string salt)
        {
            this.login = login;
            this.password = password;
            this.salt = salt;
        }

        public static explicit operator CreatorRequest?(User user)
        {
            if (user != null)
                return new CreatorRequest(user._id.ToString(), user.nickname, user.imageUrl);
            else return null;
        }
    }

    public class CreatorRequest
    {
        public string userID { get; set; }
        public string nickname { get; set; }
        public string imageUrl { get; set; }

        public CreatorRequest(string userID, string nickname, string imageUrl)
        {
            this.userID = userID;
            this.nickname = nickname;
            this.imageUrl = imageUrl;
        }
    }
}
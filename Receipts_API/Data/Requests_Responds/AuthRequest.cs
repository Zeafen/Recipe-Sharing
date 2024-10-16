namespace Recipes_API.Data.Requests_Responds
{

    public class AuthRequest
    {
        public string username { get; set; }
        public string password { get; set; }

        public AuthRequest(string username, string password)
        {
            this.username = username;
            this.password = password;
        }
    }
}

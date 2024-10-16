namespace Recipes_API.Data
{
    public class SaltedHash
    {
        public readonly string hash;
        public readonly string salt;

        public SaltedHash(string hash, string salt)
        {
            this.hash = hash;
            this.salt = salt;
        }
    }
}

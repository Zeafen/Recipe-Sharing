using Recipes_API.Data;

namespace Recipes_API.Domain.Services
{
    public interface IHashingService
    {
        public SaltedHash GenerateHash(string value, int saltLength = 32);
        public bool VerifyHash(string value, SaltedHash hash);
    }
}

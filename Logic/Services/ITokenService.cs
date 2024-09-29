using Recipes_API.Data;

namespace Recipes_API.Domain.Services
{
    public interface ITokenService
    {
        public string Generate(TokenConfig conf, List<TokenClaim> claims);

    }
}

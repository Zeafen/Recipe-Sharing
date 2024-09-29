using Microsoft.IdentityModel.Tokens;
using System.Text;

namespace Recipes_API.Data
{
    public class TokenConfig
    {
        public readonly string issuer;
        public readonly string audience;
        public readonly long expiresIn;
        public readonly string secret;

        public TokenConfig(string issuer, string audience, long expiresIn, string secret)
        {
            this.issuer = issuer;
            this.audience = audience;
            this.expiresIn = expiresIn;
            this.secret = secret;
        }

        public SymmetricSecurityKey GetSymmetricSecurityKey() => new SymmetricSecurityKey(Encoding.UTF8.GetBytes(secret));
    }

    public class TokenClaim
    {
        public readonly string name;
        public readonly string value;

        public TokenClaim(string name, string value)
        {
            this.name = name;
            this.value = value;
        }
    }
}

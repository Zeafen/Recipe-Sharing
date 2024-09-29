using Microsoft.IdentityModel.Tokens;
using Recipes_API.Data;
using Recipes_API.Domain.Services;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;

namespace Recipes_API.Domain.Implementations
{
    public class JwtTokenService : ITokenService
    {
        public string Generate(TokenConfig conf, List<TokenClaim> claims)
        {
            var key = Encoding.UTF8.GetBytes(conf.secret);
            List<Claim> _claims = new List<Claim>();
            claims.ForEach(c => _claims.Add(new Claim(c.name, c.value)));
            var token = new JwtSecurityToken(
                claims: _claims,
                issuer: conf.issuer,
                audience: conf.audience,
                expires: DateTime.UtcNow.AddMonths(1),
                signingCredentials: new SigningCredentials(
                    new SymmetricSecurityKey(Encoding.UTF8.GetBytes(conf.secret)),
                    SecurityAlgorithms.HmacSha256));
            return new JwtSecurityTokenHandler().WriteToken(token);
        }

    }   
}

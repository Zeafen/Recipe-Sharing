using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using MongoDB.Driver;
using Recipes_API.Data;
using Recipes_API.Data.Requests_Responds;
using Recipes_API.Data.User;
using Recipes_API.Domain.Services;
using System.IdentityModel.Tokens.Jwt;

namespace Recipes_API.Controllers
{
    [Route("[controller]")]
    [ApiController]
    public class AuthController : ControllerBase
    {
        private readonly IHashingService _hashingService;
        private readonly IUserDataSource _userDataSource;
        private readonly ITokenService _tokenService;
        private readonly TokenConfig _conf;
        public AuthController(IHashingService hashingService, IUserDataSource userDataSource, ITokenService service, TokenConfig conf)
        {
            _hashingService = hashingService;
            _userDataSource = userDataSource;
            _tokenService = service;
            _conf = conf;
        }

        [HttpPost("signUp")]
        public async Task<ActionResult> SignUp(AuthRequest? request)
        {
            try
            {

                if (request == null)
                    return BadRequest();
                bool areFieldsEmpty = string.IsNullOrEmpty(request.username) || string.IsNullOrEmpty(request.password);
                bool isPasswShort = request.password.Length < 10;
                bool passwContainsSpecials = request.password.Any(" !\"\'@#$%^(){}*-_=+<>.,\\/;:\'\"|?~`".Contains);
                bool passwContainsLetters = request.password.Any("QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm".Contains);
                bool passwContainsNums = request.password.Any("0123456789".Contains);
                if (areFieldsEmpty || isPasswShort || !passwContainsLetters || !passwContainsNums || !passwContainsSpecials)
                    return Conflict();
                var saltedHash = _hashingService.GenerateHash(request.password);
                var user = new User(request.username, saltedHash.hash, saltedHash.salt);
                if (!await _userDataSource.InsertUser(user))
                    return Conflict("User with that login already exists");
                return Ok();
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }
        [HttpPost("signIn")]
        public async Task<ActionResult<string>> SignIn(AuthRequest request)
        {
            try
            {
                var user = await _userDataSource.GetUserByName(request.username);
                if (user == null)
                    return Conflict();

                var isValidPassw = _hashingService.VerifyHash(
                    value: request.password,
                    hash: new Data.SaltedHash(user.password, user.salt));

                if (!isValidPassw)
                    return Conflict();

                var token = _tokenService.Generate(
                    _conf, new List<TokenClaim>
                    {
                        new TokenClaim("userID", user._id.ToString()),
                    });
                return $"Bearer {token}";
            }
            catch (Exception ex)
            {
                return Conflict(ex.Message);
            }

        }

        [HttpGet("authorize")]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public async Task<ActionResult> TryAuthorize()
        {
            var token = await HttpContext.GetTokenAsync("access_token");
            var claim = new JwtSecurityTokenHandler().ReadJwtToken(token).Claims.FirstOrDefault(c => c.Type == "userID");
            if (claim == null)
                return Conflict("Illegal token format");
            return Ok();
        }
    }
}

using Amazon.Runtime.Internal;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using MongoDB.Bson;
using Recipes_API.Data.Recipes;
using Recipes_API.Data.User;
using Recipes_API.Domain.Services;
using System.IdentityModel.Tokens.Jwt;

namespace Recipes_API.Controllers
{
    [Route("[controller]")]
    [ApiController]
    [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
    public class CreatorsController : ControllerBase
    {
        private readonly IUserDataSource _userDataSource;
        public CreatorsController(IUserDataSource userDataSource)
        {
            _userDataSource = userDataSource;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<CreatorRequest>>> GetCreators()
        {
            try
            {
                return await _userDataSource.GetCreators();
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<CreatorRequest>> GetCreatorByID(string id)
        {
            try
            {
                if (ObjectId.TryParse(id, out var creatorId))
                {
                    return (CreatorRequest)(await _userDataSource.GetUserByID(creatorId));
                }
                return Conflict();
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpPut]
        public async Task<ActionResult> UpdateCreator(CreatorRequest request)
        {
            try
            {
                var token = await HttpContext.GetTokenAsync("access_token");
                var claim = new JwtSecurityTokenHandler().ReadJwtToken(token).Claims.FirstOrDefault(c => c.Type == "userID");
                if (claim == null)
                    return Conflict();
                if (ObjectId.TryParse(claim.Value, out var userID) && ObjectId.TryParse(request.userID, out var creatorID) && userID.Equals(creatorID))
                {
                    if(await _userDataSource.UpdateUser(request))
                        return Ok();
                    return Conflict();
                }
                return Conflict();
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpGet("byname")]
        public async Task<ActionResult<IEnumerable<CreatorRequest>>> GetCreatorsByName([FromQuery(Name = "name")] string name)
        {
            try
            {
                return await _userDataSource.GetCreatorsByName(name);
            }
            catch (Exception ex)
            {
                return Conflict(ex.Message);
            }
        }

        [HttpGet("self")]
        public async Task<ActionResult<CreatorRequest>> GetUserInfo()
        {
            try
            {
                var token = await HttpContext.GetTokenAsync("access_token");
                var claim = new JwtSecurityTokenHandler().ReadJwtToken(token).Claims.FirstOrDefault(c => c.Type == "userID");
                if (claim == null)
                    return Conflict();
                if (ObjectId.TryParse(claim.Value, out var userID))
                {
                    return (CreatorRequest) await _userDataSource.GetUserByID(userID);
                }
                return Conflict();
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }

        }
    }
}

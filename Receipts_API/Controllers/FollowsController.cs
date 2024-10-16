using Amazon.Runtime;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using MongoDB.Bson;
using Recipes_API.Data.User;
using Recipes_API.Domain.Services;
using Recipes_API.Logic.Services;
using System.IdentityModel.Tokens.Jwt;
using System.Reflection.Metadata.Ecma335;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace Receipts_API.Controllers
{
    [Route("[controller]")]
    [ApiController]
    [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
    public class FollowsController : ControllerBase
    {
        private readonly IFollowersDataSource _follows;
        private readonly IUserDataSource _users;
        public FollowsController(IFollowersDataSource follows, IUserDataSource users)
        {
            _follows = follows;
            _users = users;
        }

        [HttpGet]
        public async Task<ActionResult<List<CreatorRequest>>> GetFollows()
        {
            try
            {
                var token = await HttpContext.GetTokenAsync("access_token");
                var claim = new JwtSecurityTokenHandler().ReadJwtToken(token).Claims.FirstOrDefault(c => c.Type == "userID");
                if (claim == null)
                    return Conflict("Cannot find information about your id");
                if (ObjectId.TryParse(claim.Value, out var userID))
                {
                    var follows = await _follows.GetFollows(userID);
                    List<CreatorRequest> creators = new List<CreatorRequest>();
                    foreach (var f in follows)
                    {
                        var creator = await _users.GetUserByID(f.creatorID);
                        if(creator != null)
                            creators.Add((CreatorRequest)creator);
                    };
                    return creators;
                }
                return Conflict("Cannot find information about your id");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpGet("self")]
        public async Task<ActionResult<List<CreatorRequest>>> GetFollowers()
        {
            try
            {
                var token = await HttpContext.GetTokenAsync("access_token");
                var claim = new JwtSecurityTokenHandler().ReadJwtToken(token).Claims.FirstOrDefault(c => c.Type == "userID");
                if (claim == null)
                    return Conflict("Cannot find information about your id");
                if (ObjectId.TryParse(claim.Value, out var userID))
                {
                    var follows = await _follows.GetFollowers(userID);
                    List<CreatorRequest> creators = new List<CreatorRequest>();
                    foreach(var f in follows)
                    {
                        creators.Add((CreatorRequest)await _users.GetUserByID(f.creatorID));
                    };
                    return creators;
                }
                return Conflict("Cannot find information about your id");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpGet("byname")]
        public async Task<ActionResult<IEnumerable<CreatorRequest>>> GetFollowsByName(string name)
        {
            try
            {
                var token = await HttpContext.GetTokenAsync("access_token");
                var claim = new JwtSecurityTokenHandler().ReadJwtToken(token).Claims.FirstOrDefault(c => c.Type == "userID");
                if (claim == null)
                    return Conflict();
                if (ObjectId.TryParse(claim.Value, out var userID))
                {
                    var follows = await _follows.GetFollowers(userID);
                    List<CreatorRequest> creators = new List<CreatorRequest>();
                    foreach (var f in follows)
                    {
                        var creator = (CreatorRequest)await _users.GetUserByID(f.creatorID);
                        if(creator != null && creator.nickname.Contains(name, StringComparison.OrdinalIgnoreCase))
                            creators.Add(creator);
                    };
                    return creators;
                }
                return Conflict("Cannot find information about your id");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }
          
        [HttpGet("self/{id}")]
        public async Task<ActionResult<List<CreatorRequest>>> GetCreatorFollowers(string id)
        {
            try
            {
                var token = await HttpContext.GetTokenAsync("access_token");
                var claim = new JwtSecurityTokenHandler().ReadJwtToken(token).Claims.FirstOrDefault(c => c.Type == "userID");
                if (claim == null)
                    return Conflict();
                if (ObjectId.TryParse(claim.Value, out var userID) && ObjectId.TryParse(id, out var creatorID) && !creatorID.Equals(userID))
                {
                    var follows = await _follows.GetFollowers(creatorID);
                    List<CreatorRequest> creators = new List<CreatorRequest>();
                    foreach (var f in follows)
                    {
                        CreatorRequest? follower = (CreatorRequest)await _users.GetUserByID(f.creatorID);
                        if (follower is not null)
                            creators.Add(follower);
                    };
                    return creators;
                }
                return Conflict("Cannot find information about your id");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpPost]
        public async Task<ActionResult> AddToFollows(string creatorId)
        {
            try
            {
                var token = await HttpContext.GetTokenAsync("access_token");
                var claim = new JwtSecurityTokenHandler().ReadJwtToken(token).Claims.FirstOrDefault(c => c.Type == "userID");
                if (claim == null)
                    return Conflict("Cannot find information about your id");
                if (ObjectId.TryParse(claim.Value, out var userID) && ObjectId.TryParse(creatorId, out var creatorID))
                {
                    if (await _follows.AddToFollows(new FollowerRecord(userID, creatorID)))
                        return Ok();
                    return Conflict("Already in follows");
                }
                return Conflict("Cannot find information about your id");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpDelete("{id}")]
        public async Task<ActionResult> RemoveFromFollows(string id)
        {
            try
            {
                var token = await HttpContext.GetTokenAsync("access_token");
                var claim = new JwtSecurityTokenHandler().ReadJwtToken(token).Claims.FirstOrDefault(c => c.Type == "userID");
                if (claim == null)
                    return Conflict("Cannot find information about your id");
                if (ObjectId.TryParse(claim.Value, out var userID) && ObjectId.TryParse(id, out var creatorID))
                {
                    if(await _follows.RemoveFromFollows(userID, creatorID)) return Ok();
                    return Conflict("Not in follows");
                }
                return Conflict("Cannot find information about your id");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<bool>> Follows(string id)
        {
            try
            {
                var token = await HttpContext.GetTokenAsync("access_token");
                var claim = new JwtSecurityTokenHandler().ReadJwtToken(token).Claims.FirstOrDefault(c => c.Type == "userID");
                if (claim == null)
                    return Conflict("Cannot find information about your id");
                if (ObjectId.TryParse(claim.Value, out var userID) && ObjectId.TryParse(id, out var creatorID))
                {
                    return await _follows.Follows(userID, creatorID);
                }
                return Conflict("Cannot find information about your id");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }


    }
}

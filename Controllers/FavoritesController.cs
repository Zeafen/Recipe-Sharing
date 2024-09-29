using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using MongoDB.Bson;
using Receipts_API.Data.Filtering;
using Receipts_API.Logic.Services;
using Recipes_API.Data.Recipes;
using Recipes_API.Domain.Services;
using Recipes_API.Logic.Services;
using System.IdentityModel.Tokens.Jwt;
using System.Reflection.Metadata.Ecma335;

namespace Recipes_API.Controllers
{
    [Route("[controller]")]
    [ApiController]
    [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
    public class FavoritesController : ControllerBase
    {
        private readonly IFavoritesDataSource _favorites;
        private readonly IRecipesDataSource _recipes;
        private readonly IFiltersCategoriesDataSource _filters;

        public FavoritesController(IFavoritesDataSource favorites, IRecipesDataSource recipes, IFiltersCategoriesDataSource filters)
        {
            _favorites = favorites;
            _recipes = recipes;
            _filters = filters;
        }

        [HttpGet]
        public async Task<ActionResult<List<RecipeRequest>>> GetFavorites()
        {
            try
            {
                var token = await HttpContext.GetTokenAsync("access_token");
                var claim = new JwtSecurityTokenHandler().ReadJwtToken(token).Claims.FirstOrDefault(c => c.Type == "userID");
                if (claim == null)
                    return Conflict("Incorrect format of token");
                if (ObjectId.TryParse(claim.Value, out ObjectId userId))
                {
                    var favorites = await _favorites.GetFavorites(userId);
                    List<RecipeRequest> recipes = new List<RecipeRequest>();
                    favorites.ForEach(async f =>
                    {
                        Recipe? rec = await _recipes.GetRecipeByID(f.recipeID);
                        if (rec != null)
                            recipes.Add(
                                (RecipeRequest)rec);
                    });
                    return recipes;
                }
                return Conflict("Cannot find information about your id");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<bool>> GetIsFavorite(string id)
        {
            {
                try
                {
                    var token = await HttpContext.GetTokenAsync("access_token");
                    var claim = new JwtSecurityTokenHandler().ReadJwtToken(token).Claims.FirstOrDefault(c => c.Type == "userID");
                    if (claim == null)
                        return Conflict("Cannot find information about your id");
                    if (ObjectId.TryParse(claim.Value, out ObjectId userId) && ObjectId.TryParse(id, out ObjectId recipeId))
                    {
                        return await _favorites.IsFavorite(userId, recipeId);
                    }
                    return Conflict("Cannot find information about your id");
                }
                catch (Exception ex)
                {
                    return BadRequest(ex.Message);
                }
            }
        }

        [HttpGet("byname")]
        public async Task<ActionResult<IEnumerable<RecipeRequest>>> GetFavoritesByName(string name)
        {
            try
            {
                var token = await HttpContext.GetTokenAsync("access_token");
                var claim = new JwtSecurityTokenHandler().ReadJwtToken(token).Claims.FirstOrDefault(c => c.Type == "userID");
                if (claim == null)
                    return Conflict("Cannot find information about your id");
                if (ObjectId.TryParse(claim.Value, out ObjectId userId))
                {
                    var favorites = await _favorites.GetFavorites(userId);
                    List<RecipeRequest> recipes = new List<RecipeRequest>();
                    favorites.ForEach(async f =>
                    {
                        Recipe? rec = await _recipes.GetRecipeByID(f.recipeID);
                        if (rec != null && rec.recipeName.Contains(name))
                            recipes.Add(
                                (RecipeRequest)rec);
                    });
                    return recipes;
                }
                return Conflict("Cannot find information about your id");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpGet("filtered")]
        public async Task<ActionResult<IEnumerable<RecipeRequest>>> GetFilteredFavorites(List<string> requested)
        {
            try
            {
                var token = await HttpContext.GetTokenAsync("access_token");
                var claim = new JwtSecurityTokenHandler().ReadJwtToken(token).Claims.FirstOrDefault(c => c.Type == "userID");
                if (claim == null)
                    return Conflict("Cannot find information about your id");
                if (ObjectId.TryParse(claim.Value, out ObjectId userId))
                {
                    var favorites = await _favorites.GetFavorites(userId);
                    List<RecipeRequest> recipes = new List<RecipeRequest>();
                    favorites.ForEach(async fav =>
                    {
                        var filters = await _filters.GetFiltersByRecipe(fav._id);
                        Recipe? rec = await _recipes.GetRecipeByID(fav.recipeID);
                        if(filters.All(f => requested.Any(r => r.Equals(f.filterValue))))
                        recipes.Add(
                            (RecipeRequest)rec);
                    });
                    return recipes;
                }
                return Conflict("Cannot find information about your id");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpGet("filtered/byname/{name}")]
        public async Task<ActionResult<IEnumerable<RecipeRequest>>> GetFilteredFavoritesByName(string name, List<string> requested)
        {
            try
            {
                var token = await HttpContext.GetTokenAsync("access_token");
                var claim = new JwtSecurityTokenHandler().ReadJwtToken(token).Claims.FirstOrDefault(c => c.Type == "userID");
                if (claim == null)
                    return Conflict("Cannot find information about your id");
                if (ObjectId.TryParse(claim.Value, out ObjectId userId))
                {
                    var favorites = await _favorites.GetFavorites(userId);
                    List<RecipeRequest> recipes = new List<RecipeRequest>();
                    favorites.ForEach(async fav =>
                    {
                        var filters = await _filters.GetFiltersByRecipe(fav._id);
                        Recipe? rec = await _recipes.GetRecipeByID(fav.recipeID);
                        if (filters.All(f => requested.Any(r => r.Equals(f.filterValue))) && rec.recipeName.Contains(name))
                            recipes.Add(
                                (RecipeRequest)rec);
                    });
                    return recipes;
                }
                return Conflict("Cannot find information about your id");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpPost]
        public async Task<ActionResult> AddToFavorites(string id)
        {
            try
            {
                var token = await HttpContext.GetTokenAsync("access_token");
                var claim = new JwtSecurityTokenHandler().ReadJwtToken(token).Claims.FirstOrDefault(c => c.Type == "userID");
                if (claim == null)
                    return Conflict("Cannot find information about your id");
                if (ObjectId.TryParse(claim.Value, out ObjectId userId) && ObjectId.TryParse(id, out ObjectId recipeId))
                {
                    if (await _favorites.AddToFavorites(new FavoriteRecord(userId, recipeId)))
                        return Ok();
                    return Conflict("Already in favorites");
                }
                return Conflict("Cannot find information about your id");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpDelete("{id}")]
        public async Task<ActionResult> RemoveFromFavorites(string id)
        {
            try
            {
                var token = await HttpContext.GetTokenAsync("access_token");
                var claim = new JwtSecurityTokenHandler().ReadJwtToken(token).Claims.FirstOrDefault(c => c.Type == "userID");
                if (claim != null)
                {
                    if (ObjectId.TryParse(claim.Value, out var userID) && ObjectId.TryParse(id, out var favoriteId))
                    {
                        var isDeleted = await _favorites.RemoveFromFavorites(userID, favoriteId);
                        if (isDeleted)
                            return Ok();
                        return Conflict("Not in favorites");
                    }
                    return Conflict("Cannot find information about your id");
                }
                else return Conflict("Cannot find information about your id");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }
    }
}

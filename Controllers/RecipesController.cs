using Amazon.Runtime.Internal;
using DnsClient;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using MongoDB.Bson;
using MongoDB.Driver;
using Receipts_API.Data.Filtering;
using Receipts_API.Logic.Services;
using Recipes_API.Data.Recipes;
using Recipes_API.Domain.Services;
using System.IdentityModel.Tokens.Jwt;

public class Test
{
    public List<string> requested { get; set; }
}

namespace Recipes_API.Controllers
{
    [Route("[controller]")]
    [ApiController]
    [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
    public class RecipesController : ControllerBase
    {
        private readonly IRecipesDataSource _recipes;
        private readonly IFiltersCategoriesDataSource _filters;

        public RecipesController(IRecipesDataSource recipes, IFiltersCategoriesDataSource filters)
        {
            _recipes = recipes;
            _filters = filters;
        }

        [HttpGet]
        public async Task<ActionResult<List<RecipeRequest>>> GetRecipes()
        {
            try
            {
                var token = await HttpContext.GetTokenAsync("access_token");
                var claim = new JwtSecurityTokenHandler().ReadJwtToken(token).Claims.FirstOrDefault(c => c.Type == "userID");
                if (claim == null)
                    return Conflict("Cannot find information about your id");

                if (ObjectId.TryParse(claim.Value, out var userID))
                {

                    var recipes = await _recipes.GetRecipes();
                    if (recipes == null)
                        return BadRequest();
                    return (from r in recipes where !r.creatorID.Equals(userID) select (RecipeRequest)r).ToList();
                }
                return Conflict("Incorrect claim form: userID");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpGet("own/{id}")]
        public async Task<ActionResult<bool>> GetOwns(string id)
        {
            try
            {
                var token = await HttpContext.GetTokenAsync("access_token");
                var claim = new JwtSecurityTokenHandler().ReadJwtToken(token).Claims.FirstOrDefault(c => c.Type == "userID");
                if (claim == null)
                    return Conflict("Cannot find information about your id");
                if (ObjectId.TryParse(claim.Value, out var userID) && ObjectId.TryParse(id, out var recipeID))
                {
                    return await _recipes.GetOwnsRecipe(recipeID, userID);
                }
                else
                    return Conflict("Cannot find information about your id");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpPost("filtered")]
        public async Task<ActionResult<List<RecipeRequest>>> GetFilteredRecipe([FromBody] List<string> requested)
        {
            try
            {
                var token = await HttpContext.GetTokenAsync("access_token");
                var claim = new JwtSecurityTokenHandler().ReadJwtToken(token).Claims.FirstOrDefault(c => c.Type == "userID");
                if (claim == null)
                    return Conflict("Cannot find information about your id");

                if (ObjectId.TryParse(claim.Value, out var userID))
                {
                    var recipes = (await _recipes.GetRecipes()) ?? new List<Recipe>();
                    var result = new List<RecipeRequest>();
                    foreach (var recipe in recipes)
                    {
                        var filters = await _filters.GetFiltersByRecipe(recipe._id);
                        if (requested.All(r => filters.FirstOrDefault(f => f.filterValue.Equals(r, StringComparison.OrdinalIgnoreCase)) is not null))
                            result.Add((RecipeRequest)recipe);
                    }
                    return result.Where(r => !r.creatorID.Equals(userID)).ToList();
                }
                return Conflict("Incorrect claim form: userID");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpPost("filtered/byname")]
        public async Task<ActionResult<List<RecipeRequest>>> GetFilteredRecipeByName([FromQuery(Name ="name")] string name, List<string> requested)
        {
            try
            {
                var token = await HttpContext.GetTokenAsync("access_token");
                var claim = new JwtSecurityTokenHandler().ReadJwtToken(token).Claims.FirstOrDefault(c => c.Type == "userID");
                if (claim == null)
                    return Conflict("Cannot find information about your id");

                if (ObjectId.TryParse(claim.Value, out var userID))
                {
                    var recipes = (await _recipes.GetRecipes()) ?? new List<Recipe>();
                    var result = new List<RecipeRequest>();
                    foreach (var recipe in recipes)
                    {
                        var filters = await _filters.GetFiltersByRecipe(recipe._id);
                        if (requested.All(r => filters.FirstOrDefault(f => f.filterValue.Equals(r, StringComparison.OrdinalIgnoreCase)) is not null))
                            result.Add((RecipeRequest)recipe);
                    }
                    return (from r in result where r.recipeName.Contains(name, StringComparison.OrdinalIgnoreCase) && !r.creatorID.Equals(userID) select r).ToList();
                }
                return Conflict("Incorrect claim form: userID");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpGet("{id}")]
        public async  Task<ActionResult<RecipeRequest>> GetRecipeById(string? id)
        {
            try
            {
                if (id is null)
                    return Conflict();

                ObjectId requiredId;
                if(!ObjectId.TryParse(id, out requiredId))
                    return Conflict("Cannot find information about your id");

                var recipe = await _recipes.GetRecipeByID(requiredId);
                if(recipe == null)
                    return BadRequest();
                return (RecipeRequest)recipe;
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpGet("bycreator/{id}")]
        public async Task<ActionResult<List<RecipeRequest>>> GetRecipesByCreator(string id)
        {
            try
            {
                if(ObjectId.TryParse(id, out var creatorId))
                {
                    var recipes = await _recipes.GetRecipesByCreator(creatorId);
                    if (recipes == null)
                        return BadRequest();
                    var respond = from r in recipes
                                  select (RecipeRequest)r;
                    return respond.ToList(); 
                }
                else
                    return Conflict("Incorrect format of creator id");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpGet("bycreator/{id}/byname")]
        public async Task<ActionResult<List<RecipeRequest>>> GetRecipesByCreatorByName(string id, [FromQuery(Name = "name")] string name)
        {
            try
            {
                if(id is null)
                    return Conflict("Incorrect format of id");
                if (ObjectId.TryParse(id, out var creatorId))
                {
                    var recipes = await _recipes.GetRecipesByCreator(creatorId);
                    if (recipes == null)
                        return BadRequest();
                    var respond = from r in recipes
                                  select (RecipeRequest)r;

                    return (from r in respond where r.recipeName.Contains(name, StringComparison.OrdinalIgnoreCase) select r).ToList();
                }
                else
                    return Conflict("Incorrect format of creator id");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpPost("bycreator/{id}/filtered")]
        public async Task<ActionResult<List<RecipeRequest>>> GetFilteredCreatorRecipes(string id, List<string> requested)
        {
            try
            {
                var recipes = (await _recipes.GetRecipes()) ?? new List<Recipe>();
                var result = new List<RecipeRequest>();
                foreach (var recipe in recipes)
                {
                    var filters = await _filters.GetFiltersByRecipe(recipe._id);
                    if (requested.All(r => filters.FirstOrDefault(f => f.filterValue.Equals(r, StringComparison.OrdinalIgnoreCase)) is not null))
                        result.Add((RecipeRequest)recipe);
                }
                return result.OrderBy(r => r.creatorID.Equals(id)).ToList();
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }
        [HttpPost("bycreator/{id}/filtered/byname")]
        public async Task<ActionResult<List<RecipeRequest>>> GetFilteredCreatorRecipes(string id, [FromQuery(Name = "name")] string name, List<string> requested)
        {
            try
            {
                var recipes = (await _recipes.GetRecipes()) ?? new List<Recipe>();
                var result = new List<RecipeRequest>();
                foreach (var recipe in recipes)
                {
                    var filters = await _filters.GetFiltersByRecipe(recipe._id);
                    if (requested.All(r => filters.FirstOrDefault(f => f.filterValue.Equals(r, StringComparison.OrdinalIgnoreCase)) is not null))
                        result.Add((RecipeRequest)recipe);
                }
                return result.OrderBy(r => r.creatorID.Equals(id) && r.recipeName.Contains(name, StringComparison.OrdinalIgnoreCase)).ToList();
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpGet("byname")]
        public async Task<ActionResult<List<RecipeRequest>>> GetRecipesByName([FromQuery(Name = "name")]  string? name)
        {
            try
            {
                var token = await HttpContext.GetTokenAsync("access_token");
                var claim = new JwtSecurityTokenHandler().ReadJwtToken(token).Claims.FirstOrDefault(c => c.Type == "userID");
                if (claim == null)
                    return Conflict("Cannot find information about your id");

                if (ObjectId.TryParse(claim.Value, out var userID))
                {
                    if (string.IsNullOrEmpty(name))
                        return Conflict();
                    var recipes = await _recipes.GetRecipesByName(name);
                    if (recipes == null)
                        return BadRequest();
                    return (from r in recipes where !r.creatorID.Equals(userID) select (RecipeRequest)r).ToList();
                }
                return Conflict("Incorrect claim form: userID");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpPost]
        public async Task<ActionResult> PostRecipe(RecipeRequest request)
        {
            try
            {
                var token = await HttpContext.GetTokenAsync("access_token");
                var claims = new JwtSecurityTokenHandler().ReadJwtToken(token).Claims;
                var claim = new JwtSecurityTokenHandler().ReadJwtToken(token).Claims.FirstOrDefault(c => c.Type == "userID");
                if(claim == null)
                    return Conflict("Cannot find information about your id");
                if (ObjectId.TryParse(claim.Value, out var userID))
                {

                    var recipe = new Recipe(request.imageUrl, request.recipeName, request.description, request.ingredients, request.steps, userID);
                    var wasAcknowledged = await _recipes.InsertRecipe(recipe);
                    if (wasAcknowledged)
                        return Ok();
                    return BadRequest();
                }
                else
                    return Conflict("Cannot find information about your id");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpDelete("{id}")]
        public async Task<ActionResult> DeleteRecipe(string? id)
        {
            try
            {
                if(string.IsNullOrEmpty(id))
                    return BadRequest();
                if (ObjectId.TryParse(id, out var recipeId))
                {
                    var recipe = await _recipes.GetRecipeByID(recipeId);
                    if(recipe == null)
                        return NotFound();

                    var token = await HttpContext.GetTokenAsync("access_token");
                    var userID = new JwtSecurityTokenHandler().ReadJwtToken(token).Claims.FirstOrDefault(c => c.Type == "userID");
                    if (userID == null || !recipe.creatorID.Equals(userID))
                        return Conflict();
                    if (!await _recipes.DeleteRecipe(recipeId))
                        return Conflict();
                    return Ok();
                }
                else
                    return Conflict("Incorrect format of id");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpPut]
        public async Task<ActionResult> UpdateRecipe(RecipeRequest recipe)
        {
            try
            {
                if (ObjectId.TryParse(recipe.recipeID, out var recipeID))
                {

                    var updated = await _recipes.GetRecipeByID(recipeID);
                    var token = await HttpContext.GetTokenAsync("access_token");
                    var claim = new JwtSecurityTokenHandler().ReadJwtToken(token).Claims.FirstOrDefault(c => c.Type == "userID");
                    if(claim is null)
                        return Conflict("Cannot find info about your id");
                    if (claim == null || updated == null || !ObjectId.TryParse(claim.Value, out var userID) || !updated.creatorID.Equals(userID))
                        return Conflict();
                    if (!await _recipes.UpdateRecipe(
                            new Recipe(recipeID, updated.creatorID, recipe.imageUrl, recipe.recipeName, recipe.description, recipe.ingredients, recipe.steps))
                        )
                        return Conflict();
                    return Ok();
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

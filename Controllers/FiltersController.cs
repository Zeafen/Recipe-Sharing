using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.ApiExplorer;
using MongoDB.Bson;
using Receipts_API.Data.Filtering;
using Receipts_API.Logic.Services;
using System.Text;

namespace Receipts_API.Controllers
{
    [Route("[controller]")]
    [ApiController]
    [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
    public class FiltersController : ControllerBase
    {
        private IFiltersCategoriesDataSource _filters;

        public FiltersController(IFiltersCategoriesDataSource filters)
        {
            _filters = filters;
        }

        [HttpGet("categories")]
        public async Task<ActionResult<List<string>>> GetCategories()
        {
            try
            {
                var result = await _filters.GetCategories();
                return (from r in result select r.categoryName).ToList();
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpGet("categorized")]
        public async Task<ActionResult<Dictionary<string, List<string>>>> GetCategorizedFilters()
        {
            try
            {
                return await _filters.GetCategorizedFilters();
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpGet("bycategory")]
        public async Task<ActionResult<List<string>>> GetFiltersByCategory(string categoryName)
        {
            try
            {
                var category = (await _filters.GetCategories()).Find(c => c.categoryName.Equals(categoryName, StringComparison.OrdinalIgnoreCase));
                if (category != null)
                {
                    return (from f in await _filters.GetFiltersByCategory(category._id)
                            select f.filterValue).ToList();
                }
                return Conflict("Cannot find such category");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpGet("byrecipe/{id}")]
        public async Task<ActionResult<List<string>>> GetRecipeFilters(string id)
        {
            try
            {
                if (ObjectId.TryParse(id, out var recipeId))
                    return (from f in await _filters.GetFiltersByRecipe(recipeId) select f.filterValue).ToList();

                return Conflict("Cannot find such recipe");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpPost]
        public async Task<ActionResult> AttachFiltersToRecipe(FiltersRequest request)
        {
            try
            {
                if (ObjectId.TryParse(request.recipeID, out var recipeId))
                {
                    foreach (var filter in request.filters)
                        await _filters.AttachFilterToRecipe(recipeId, filter);
                    return Ok();
                }
                return Conflict("Incorrect format of id");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpDelete]
        public async Task<ActionResult> RemoveFilterToRecipe(FiltersRequest request)
        {
            try
            {
                if (ObjectId.TryParse(request.recipeID, out var recipeId))
                {
                    foreach (var filter in request.filters)
                        await _filters.RemoveFilterFromRecipe(recipeId, filter);
                    return Ok();
                }
                return Conflict("Incorrect format of id");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpDelete("{id}")]
        public async Task<ActionResult> ClearRecipeFilters(string id)
        {
            try
            {
                if (ObjectId.TryParse(id, out var recipeId))
                {
                    if(await _filters.ClearRecipeFilters(recipeId))
                        return Ok();
                    return BadRequest("Cannot clear recipe filters.");
                    
                }
                return Conflict("Incorrect format of id");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }
        
    }
}

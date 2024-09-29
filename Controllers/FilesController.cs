using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System.Text;

namespace Recipes_API.Controllers
{
    [Route("[controller]")]
    [ApiController]
    [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
    public class FilesController : ControllerBase
    {
        private readonly string staticFilesPath;
        public FilesController(IWebHostEnvironment env)
        {
            staticFilesPath = $@"{env.ContentRootPath}\static";
        }


        
        [HttpPost("recipes")]
        public async Task<ActionResult<string>> PostRecipesFiles()
        {
            try
            {
                IFormFile? file = Request.Form.Files.FirstOrDefault();
                if (file is null || file.Length == 0)
                    return Conflict("Cannot save file: file is empty or does not exist");
                if (file.Name!.Equals("image") || file.Name!.Equals("picture"))
                    return Conflict("Incorrect request format");
                string newFileName = Path.GetFileName(file.FileName) + DateTime.Now.Ticks.ToString() + ".png";
                using (var stream = System.IO.File.Create($@"{staticFilesPath}\images\recipes\{newFileName}"))
                {
                    await file.CopyToAsync(stream);
                }
                return $@"{Request.Scheme}://{Request.Host.Value}/static/images/recipes{newFileName}";
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpPost("creators")]
        public async Task<ActionResult<string>> PostCreatorsFiles()
        {
            try
            {
                IFormFile? file = Request.Form.Files.FirstOrDefault();
                if (file is null || file.Length == 0)
                    return Conflict("Cannot save file: file is empty or does not exist");
                if (file.Name!.Equals("image") || file.Name!.Equals("picture"))
                    return Conflict("Incorrect request format");
                string newFileName = Path.GetFileName(file.FileName) + DateTime.Now.Ticks.ToString() + ".png";
                using (var stream = System.IO.File.Create($@"{staticFilesPath}\images\creators\{newFileName}"))
                {
                    await file.CopyToAsync(stream);
                }
                return $@"{Request.Scheme}://{Request.Host.Value}/static/images/creators/{newFileName}";
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }
    }
}

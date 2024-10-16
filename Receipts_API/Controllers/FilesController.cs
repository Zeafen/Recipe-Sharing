using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using MongoDB.Bson;
using Recipes_API.Data.User;
using Recipes_API.Domain.Services;
using System.IdentityModel.Tokens.Jwt;
using System.Text;

namespace Recipes_API.Controllers
{
    [Route("[controller]")]
    [ApiController]
    [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
    public class FilesController : ControllerBase
    {
        private readonly IUserDataSource _users;
        private readonly string staticFilesPath;
        public FilesController(IWebHostEnvironment env, IUserDataSource users)
        {
            staticFilesPath = $@"{env.ContentRootPath}\static";
            _users = users;
        }


        
        [HttpPost("recipes")]
        public async Task<ActionResult<string>> PostRecipesFiles()
        {
            try
            {
                IFormFile? file = Request.Form.Files.FirstOrDefault();
                if (file is null || file.Length == 0)
                    return Conflict("Cannot save file: file is empty or does not exist");
                if (!(file.Name.Equals("image") || file.Name.Equals("picture")))
                    return Conflict("Incorrect request format");
                string newFileName = Path.GetFileName(file.FileName) + DateTime.Now.Ticks.ToString() + ".png";
                using (var stream = System.IO.File.Create($@"{staticFilesPath}\images\recipes\{newFileName}"))
                {
                    await file.CopyToAsync(stream);
                }
                return $@"{Request.Scheme}://{Request.Host.Value}/static/images/recipes/{newFileName}";
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
                var token = await HttpContext.GetTokenAsync("access_token");
                var claim = new JwtSecurityTokenHandler().ReadJwtToken(token).Claims.FirstOrDefault(c => c.Type == "userID");
                if (claim == null)
                    return Conflict("Cannot find information about your id");

                if (ObjectId.TryParse(claim.Value, out var userID))
                {
                    var userInfo = await _users.GetUserByID(userID);

                    if(!string.IsNullOrEmpty(userInfo.imageUrl))
                        DeleteExistingCreatorFile(string.Concat(userInfo.imageUrl
                            .Reverse()
                            .TakeWhile(ch => !(ch == '/' || ch == '\\'))
                            .Reverse()));

                    IFormFile? file = Request.Form.Files.FirstOrDefault();
                    if (file is null || file.Length == 0)
                        return Conflict("Cannot save file: file is empty or does not exist");
                    if (!(file.Name.Equals("image") || file.Name.Equals("picture")))
                        return Conflict("Incorrect request format");
                    string newFileName = Path.GetFileName(file.FileName) + DateTime.Now.Ticks.ToString() + ".png";
                    using (var stream = System.IO.File.Create($@"{staticFilesPath}\images\creators\{newFileName}"))
                    {
                        await file.CopyToAsync(stream);
                    }

                    string imageUrl = $@"{Request.Scheme}://{Request.Host.Value}/static/images/creators/{newFileName}";
                    userInfo.imageUrl = imageUrl;

                    await _users.UpdateUser((CreatorRequest)userInfo);
                    return imageUrl;

                }
                return Conflict("Incorrect token form");
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        private bool DeleteExistingCreatorFile(string fileName)
        {
            try
            {
                new FileInfo($"{staticFilesPath}/images/creators/{fileName}").Delete();
                return true;
            }
            catch (Exception)
            {
                return false;
            }
        }
    }
}

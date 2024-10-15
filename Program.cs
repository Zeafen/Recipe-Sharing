using Amazon.Runtime.Internal.Transform;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.Extensions.FileProviders;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi.Models;
using MongoDB.Driver;
using Receipts_API.Logic.Implementations;
using Receipts_API.Logic.Services;
using Recipes_API.Data;
using Recipes_API.Domain.Implementations;
using Recipes_API.Domain.Services;
using Recipes_API.Logic.Implementations;
using Recipes_API.Logic.Services;
using System.Text;


var builder = WebApplication.CreateBuilder(args);
var conf = new TokenConfig(builder.Configuration["JWT:issuer"], builder.Configuration["JWT:audience"], (long)1000 * 60 * 60 * 24 * 30, builder.Configuration["JWT:secret"]);
builder.Services.AddAuthorization();
builder.Services.AddAuthentication(opts =>
{
    opts.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
    opts.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
    opts.DefaultScheme = JwtBearerDefaults.AuthenticationScheme;
})
    .AddJwtBearer(opts =>
    {
        opts.TokenValidationParameters = new TokenValidationParameters
        {
            ValidIssuer = conf.issuer,
            ValidAudience = conf.audience,
            IssuerSigningKey = conf.GetSymmetricSecurityKey(),
            ValidateIssuer = true,
            ValidateAudience = true,
            ValidateIssuerSigningKey = true,
            ValidateLifetime = true
        };
    });

string CONNECTION_STRING = "<Connection_String>";
var settings = MongoClientSettings.FromConnectionString($"{CONNECTION_STRING}");
settings.ServerApi = new ServerApi(ServerApiVersion.V1);

builder.Services.AddSingleton(new MongoClient(settings));
builder.Services.AddSingleton(conf);
builder.Services.AddSingleton<IHashingService, SHA256HashingService>();
builder.Services.AddSingleton<ITokenService, JwtTokenService>();
builder.Services.AddSingleton<IUserDataSource, MongoUserDataSource>(opts => new MongoUserDataSource(opts.GetService<MongoClient>()));
builder.Services.AddSingleton<IRecipesDataSource, MongoRecipesDataSource>(opts => new MongoRecipesDataSource(opts.GetService<MongoClient>()));
builder.Services.AddSingleton<IFavoritesDataSource, MongoFavoritesDataSource>(opts => new MongoFavoritesDataSource(opts.GetService<MongoClient>()));
builder.Services.AddSingleton<IFollowersDataSource, MongoFollowersDataSource>(opts => new MongoFollowersDataSource(opts.GetService<MongoClient>()));
builder.Services.AddSingleton<IFiltersCategoriesDataSource, MongoFiltersCategoriesDataSource>(opts => new MongoFiltersCategoriesDataSource(opts.GetService<MongoClient>()));
// Add services to the container.
builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();
// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}
app.UseHttpsRedirection();
app.UseStaticFiles(new StaticFileOptions
{
    FileProvider = new PhysicalFileProvider(Path.Combine(builder.Environment.ContentRootPath, "static")),
    RequestPath = "/static"
});
app.UseAuthorization();
app.UseAuthentication();
app.MapControllers();
app.Run();
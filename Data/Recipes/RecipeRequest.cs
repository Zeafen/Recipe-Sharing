using MongoDB.Bson;

namespace Recipes_API.Data.Recipes
{
    public class RecipeRequest
    {
        public string recipeID { get; set; }
        public string creatorID { get; set; }
        public string? imageUrl { get; set; }
        public string recipeName { get; set; }
        public string? description { get; set; }
        public List<Ingredient> ingredients { get; set; }
        public List<Step> steps { get; set; }

        public RecipeRequest(string recipeID, string creatorID, string? imageUrl, string recipeName, string? description, List<Ingredient> ingredients, List<Step> steps)
        {
            this.recipeID = recipeID;
            this.imageUrl = imageUrl;
            this.recipeName = recipeName;
            this.description = description;
            this.ingredients = ingredients;
            this.steps = steps;
            this.creatorID = creatorID;
        }

        public RecipeRequest(string recipeID, string creatorID, string? imageUrl, string recipeName, string? description) :
            this(recipeID, creatorID, imageUrl, recipeName, description, new List<Ingredient>(), new List<Step>())
        {

        }
        public RecipeRequest()
        {

        }
    }
}

using MongoDB.Bson;

namespace Recipes_API.Data.Recipes
{
    public class Recipe
    {
        public ObjectId _id { get; set; } = ObjectId.GenerateNewId();
        public ObjectId creatorID { get; set; }
        public string? imageUrl { get; set; }
        public string recipeName { get; set; }
        public string? description { get; set; }
        public List<Ingredient> ingredients { get; set; }
        public List<Step> steps { get; set; }

        public Recipe(ObjectId recipeID, ObjectId creatorID, string? imageUrl, string recipeName, string? description, List<Ingredient> ingredients, List<Step> steps)
        {
            this._id = recipeID;
            this.creatorID = creatorID;
            this.imageUrl = imageUrl;
            this.recipeName = recipeName;
            this.description = description;
            this.ingredients = ingredients;
            this.steps = steps;
        }

        public Recipe(string? imageUrl, string recipeName, string? description, List<Ingredient> ingredients, List<Step> steps, ObjectId creatorID)
        {
            this.imageUrl = imageUrl;
            this.recipeName = recipeName;
            this.description = description;
            this.ingredients = ingredients;
            this.steps = steps;
            this.creatorID = creatorID;
        }

        public Recipe(string? imageUrl, string recipeName, string? description, ObjectId creatorID) :
            this(imageUrl, recipeName, description, new List<Ingredient>(), new List<Step>(), creatorID)
        {

        }

        public static explicit operator RecipeRequest(Recipe recipe)
        {
            return new RecipeRequest(recipe._id.ToString(), recipe.creatorID.ToString(), recipe.imageUrl, recipe.recipeName, recipe.description, recipe.ingredients, recipe.steps); ; ;
        }
    }
}

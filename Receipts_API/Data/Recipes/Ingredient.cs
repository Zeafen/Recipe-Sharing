using MongoDB.Bson;
namespace Recipes_API.Data.Recipes
{
    public class Ingredient
    {
        public string name { get; set; }
        public long amount { get; set; }
        public string measureType { get; set; }

        public Ingredient() { } 
    }
}

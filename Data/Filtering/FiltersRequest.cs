namespace Receipts_API.Data.Filtering
{
    public class FiltersRequest
    {
        public string recipeID { get; set; }
        public List<string> filters { get; set; }

        public FiltersRequest(string recipeID, List<string> filters)
        {
            this.recipeID = recipeID;
            this.filters = filters;
        }
    }
}

using System.Text;

namespace Recipes_API.Domain.Helpers
{
    public static class RandomHelper
    {
        private static Random _random = new Random();
        public static string RandomString(int length)
        {
            int randVal;
            char letter;
            var builder = new StringBuilder(length);
            for (int i = 0; i < length; i++)
            {
                randVal = _random.Next(0,26);
                letter = Convert.ToChar(randVal + 65);
                builder.Append(letter);
            }
            return builder.ToString();
        }
    }
}

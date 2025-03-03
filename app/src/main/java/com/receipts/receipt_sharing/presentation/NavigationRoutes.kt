import kotlinx.serialization.Serializable

sealed interface NavigationRoutes {

    sealed interface Auth : NavigationRoutes {

        @Serializable
        object AuthFolder : Auth

        @Serializable
        object RegisterPage : Auth

        @Serializable
        object AuthorizePage : Auth

        @Serializable
        object TryAuthorizePage : Auth

        @Serializable
        object ForgotPasswordPage : Auth
    }

    sealed interface Creators : NavigationRoutes {
        @Serializable
        object CreatorsFolder : Creators

        @Serializable
        object CreatorsScreen : Creators

        @Serializable
        object FollowsScreen : Creators

        @Serializable
        object FollowersScreen : Creators

        @Serializable
        object UserInfoPage : Creators

        @Serializable
        data class CreatorPage(val creatorID: String) : Creators
    }

    sealed interface Recipes : NavigationRoutes {
        @Serializable
        object RecipesFolder : Recipes

        @Serializable
        object FavoritesPage : Recipes

        @Serializable
        object RecipesScreen : Recipes

        @Serializable
        object OwnRecipesScreen : Recipes

        @Serializable
        data class FiltersSelection(
            val isRecipePage: Boolean = false
        ) : Recipes

        @Serializable
        data class CreatorRecipesScreen(val creatorID: String) : Recipes

        @Serializable
        data class RecipePage(val recipeID: String) : Recipes

        @Serializable
        object RecipeAddingPage : Recipes

        @Serializable
        data class ReviewsScreen(val recipeID: String) : Recipes

        @Serializable
        data class ReviewPage(val recipeID: String) : Recipes

    }
}
import kotlinx.serialization.Serializable

sealed interface NavigationRoutes {

    sealed interface Auth : NavigationRoutes {

        @Serializable
        data object AuthFolder : Auth

        @Serializable
        data object RegisterPage : Auth

        @Serializable
        data object AuthorizePage : Auth

        @Serializable
        data object TryAuthorizePage : Auth

        @Serializable
        data object ForgotPasswordPage : Auth
    }

    sealed interface Creators : NavigationRoutes {
        @Serializable
        data object CreatorsFolder : Creators

        @Serializable
        data object CreatorsScreen : Creators

        @Serializable
        data object FollowsScreen : Creators

        @Serializable
        data object FollowersScreen : Creators

        @Serializable
        data object ProfilePage : Creators

        @Serializable
        data class CreatorPage(val creatorID: String) : Creators
    }

    sealed interface Recipes : NavigationRoutes {
        @Serializable
        data object HomePage : Recipes

        @Serializable
        data object RecipesFolder : Recipes

        @Serializable
        data object FavoritesPage : Recipes

        @Serializable
        data object RecipesScreen : Recipes

        @Serializable
        data object OwnRecipesScreen : Recipes

        @Serializable
        data class CreatorRecipesScreen(val creatorID: String) : Recipes

        @Serializable
        data class RecipePage(val recipeID: String?) : Recipes


        @Serializable
        data class ReviewsScreen(val recipeID: String) : Recipes

        @Serializable
        data class ReviewPage(val recipeID: String) : Recipes

    }
}
package com.receipts.receipt_sharing

import NavigationRoutes
import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.receipts.RecipesApplication
import com.receipts.receipt_sharing.data.dataStore.UserInfo
import com.receipts.receipt_sharing.data.repositoriesImpl.authDataStore
import com.receipts.receipt_sharing.domain.apiServices.UnsafeImageLoader
import com.receipts.receipt_sharing.domain.response.AuthResult
import com.receipts.receipt_sharing.presentation.auth.AuthEvent
import com.receipts.receipt_sharing.presentation.auth.AuthViewModel
import com.receipts.receipt_sharing.presentation.creators.CreatorPageEvent
import com.receipts.receipt_sharing.presentation.creators.CreatorPageViewModel
import com.receipts.receipt_sharing.presentation.creators.CreatorsScreenEvent
import com.receipts.receipt_sharing.presentation.creators.CreatorsScreenViewModel
import com.receipts.receipt_sharing.presentation.creators.ProfilePageEvent
import com.receipts.receipt_sharing.presentation.creators.ProfileViewModel
import com.receipts.receipt_sharing.presentation.recipes.RecipePageEvent
import com.receipts.receipt_sharing.presentation.recipes.RecipePageViewModel
import com.receipts.receipt_sharing.presentation.recipes.RecipesScreenEvent
import com.receipts.receipt_sharing.presentation.recipes.RecipesScreenViewModel
import com.receipts.receipt_sharing.presentation.reviews.ReviewPageEvent
import com.receipts.receipt_sharing.presentation.reviews.ReviewPageViewModel
import com.receipts.receipt_sharing.presentation.reviews.ReviewsScreenEvent
import com.receipts.receipt_sharing.presentation.reviews.ReviewsScreenViewModel
import com.receipts.receipt_sharing.ui.auth.ForgotPasswordPage
import com.receipts.receipt_sharing.ui.auth.LoginScreen
import com.receipts.receipt_sharing.ui.auth.RegisterScreen
import com.receipts.receipt_sharing.ui.creators.profile.CreatorConfigPage
import com.receipts.receipt_sharing.ui.filters.FiltersPage
import com.receipts.receipt_sharing.ui.reviews.ReviewPage
import com.receipts.receipt_sharing.ui.reviews.ReviewsScreen
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme
import kotlinx.coroutines.launch

data class RecipeNavigationItem(
    val route: NavigationRoutes,
    val nameID: Int,
    val iconID: Int
)


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.INTERNET
                )
            else
                arrayOf(Manifest.permission.INTERNET),
            0
        )


        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            val navItems = listOf(
                RecipeNavigationItem(
                    NavigationRoutes.Recipes.RecipesFolder,
                    R.string.recipes_page_title,
                    R.drawable.recipes_page_ic
                ),
                RecipeNavigationItem(
                    NavigationRoutes.Recipes.OwnRecipesScreen,
                    R.string.own_recipes_page_title,
                    R.drawable.own_recipes_page_ic
                ),
                RecipeNavigationItem(
                    NavigationRoutes.Recipes.FavoritesPage,
                    R.string.favorites_page_title,
                    R.drawable.in_favorite_ic
                ),
                RecipeNavigationItem(
                    NavigationRoutes.Creators.CreatorsFolder,
                    R.string.creator_page_header,
                    R.drawable.creators_page_ic
                ),
                RecipeNavigationItem(
                    NavigationRoutes.Creators.FollowsScreen,
                    R.string.follows_page_title,
                    R.drawable.follows_page_ic
                ),
                RecipeNavigationItem(
                    NavigationRoutes.Creators.UserInfoPage,
                    R.string.user_info_page_title,
                    R.drawable.user_info_page_ic
                )
            )

            RecipeSharing_theme {
                Scaffold {
                    ModalNavigationDrawer(modifier = Modifier.padding(it),
                        drawerState = drawerState,
                        drawerContent = {
                            val userState by context.authDataStore.data.collectAsState(initial = UserInfo())
                            ModalDrawerSheet(modifier = Modifier) {
                                if (userState.token.isNullOrEmpty()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Button(onClick = {
                                            navController.popBackStack()
                                            navController.navigate(NavigationRoutes.Auth.AuthFolder)
                                        }) {
                                            Text(text = stringResource(id = R.string.unauthorized_txt))
                                        }
                                    }
                                } else {
                                    if (userState.imageUrl.isNullOrEmpty())
                                        Image(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .heightIn(min = 100.dp, max = 200.dp),
                                            painter = painterResource(id = R.drawable.no_image),
                                            contentDescription = "",
                                            contentScale = ContentScale.Crop
                                        )
                                    else
                                        AsyncImage(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .heightIn(min = 100.dp, max = 200.dp)
                                                .padding(8.dp),
                                            model = ImageRequest.Builder(context)
                                                .data(userState.imageUrl)
                                                .crossfade(true)
                                                .build(),
                                            imageLoader = UnsafeImageLoader.getInstance(),
                                            contentScale = ContentScale.Fit,
                                            contentDescription = "",
                                        )
                                    Text(
                                        modifier = Modifier
                                            .padding(start = 16.dp, top = 8.dp, bottom = 32.dp),
                                        text = userState.userName,
                                        style = MaterialTheme.typography.headlineLarge
                                    )
                                    HorizontalDivider()

                                    navItems.indices.forEach {
                                        NavigationDrawerItem(
                                            label = { Text(text = stringResource(id = navItems[it].nameID)) },
                                            icon = {
                                                Icon(
                                                    painterResource(id = navItems[it].iconID),
                                                    contentDescription = ""
                                                )
                                            },
                                            selected = userState.lastSelectedPageInd == it,
                                            onClick = {
                                                try {
                                                    navController.popBackStack()
                                                    navController.navigate(navItems[it].route)
                                                    scope.launch {
                                                        context.authDataStore.updateData { uf ->
                                                            uf.copy(lastSelectedPageInd = it)
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    Toast.makeText(
                                                        context,
                                                        context.getText(R.string.no_such_route),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                } finally {
                                                    scope.launch {
                                                        drawerState.apply {
                                                            drawerState.close()
                                                        }
                                                    }
                                                }
                                            }
                                        )
                                        HorizontalDivider()
                                    }
                                }
                            }
                        }
                    ) {
                        SharedTransitionLayout {
                            NavHost(
                                navController = navController,
                                startDestination = NavigationRoutes.Auth.AuthFolder
                            ) {
                                navigation<NavigationRoutes.Auth.AuthFolder>(
                                    startDestination = NavigationRoutes.Auth.TryAuthorizePage
                                )
                                {
                                    composable<NavigationRoutes.Auth.RegisterPage> {
                                        val parentEntry =
                                            remember { navController.getBackStackEntry<NavigationRoutes.Auth.AuthFolder>() }
                                        val VM = viewModel<AuthViewModel>(
                                            viewModelStoreOwner = parentEntry,
                                            factory = RecipesApplication.appModule.authVMFactory
                                        )
                                        val state by VM.state.collectAsState()
                                        RegisterScreen(
                                            onGotoLogin = {
                                                navController.navigate(NavigationRoutes.Auth.AuthorizePage)
                                            },
                                            state = state,
                                            onEvent = VM::onEvent,
                                            onOpenMenu = {
                                                scope.launch {
                                                    drawerState.apply {
                                                        if (drawerState.isOpen) drawerState.close()
                                                        else drawerState.open()
                                                    }
                                                }
                                            },
                                            onAuthorizationFinished = {
                                                navController.navigate(NavigationRoutes.Recipes.RecipesFolder)
                                            })
                                    }
                                    composable<NavigationRoutes.Auth.AuthorizePage> {
                                        val parentEntry =
                                            remember { navController.getBackStackEntry<NavigationRoutes.Auth.AuthFolder>() }
                                        val authVm =
                                            viewModel<AuthViewModel>(
                                                parentEntry,
                                                factory = RecipesApplication.appModule.authVMFactory
                                            )
                                        val state by authVm.state.collectAsState()

                                        LoginScreen(
                                            onGotoRegister = {
                                                navController.navigate(
                                                    NavigationRoutes.Auth.RegisterPage
                                                )
                                            },
                                            state = state,
                                            onEvent = authVm::onEvent,
                                            onOpenMenu = {
                                                scope.launch {
                                                    drawerState.apply {
                                                        if (drawerState.isOpen) drawerState.close()
                                                        else drawerState.open()
                                                    }
                                                }
                                            },
                                            onAuthorizationFinished = {
                                                navController.navigate(
                                                    NavigationRoutes.Recipes.RecipesFolder
                                                )
                                            },
                                            onGoToChangePassword = {
                                                navController.navigate(NavigationRoutes.Auth.ForgotPasswordPage)
                                            })
                                    }
                                    composable<NavigationRoutes.Auth.ForgotPasswordPage> {
                                        val parentEntry = remember {
                                            navController.getBackStackEntry<NavigationRoutes.Auth.AuthFolder>()
                                        }
                                        val viewModel: AuthViewModel = viewModel(
                                            viewModelStoreOwner = parentEntry,
                                            factory = RecipesApplication.appModule.authVMFactory
                                        )
                                        LaunchedEffect(Unit) {
                                            viewModel.onEvent(AuthEvent.ClearData)
                                        }
                                        val state by viewModel.state.collectAsState()

                                        LaunchedEffect(state.infoMessage) {
                                            if (!state.infoMessage.isNullOrEmpty()) {
                                                Toast.makeText(
                                                    context,
                                                    state.infoMessage,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                viewModel.onEvent(AuthEvent.ClearMessage)
                                            }
                                        }

                                        ForgotPasswordPage(
                                            state = state,
                                            onEvent = viewModel::onEvent,
                                            onGoBackClick = { navController.navigateUp() }
                                        )
                                    }
                                    composable<NavigationRoutes.Auth.TryAuthorizePage> {
                                        val parentEntry =
                                            remember { navController.getBackStackEntry<NavigationRoutes.Auth.AuthFolder>() }
                                        val authVm =
                                            viewModel<AuthViewModel>(
                                                parentEntry,
                                                factory = RecipesApplication.appModule.authVMFactory
                                            )
                                        val state by authVm.state.collectAsState()

                                        when (state.result) {
                                            is AuthResult.Authorized -> {
                                                navController.popBackStack()
                                                navController.navigate(NavigationRoutes.Recipes.RecipesFolder)
                                            }

                                            is AuthResult.Error -> {
                                                navController.popBackStack()
                                                navController.navigate(NavigationRoutes.Auth.AuthorizePage)
                                                authVm.onEvent(AuthEvent.ClearData)
                                                Toast.makeText(
                                                    context,
                                                    R.string.token_expired,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }

                                            is AuthResult.Loading -> {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(MaterialTheme.colorScheme.surface),
                                                    verticalArrangement = Arrangement.Center,
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier
                                                            .size(84.dp),
                                                        strokeWidth = 8.dp
                                                    )
                                                }
                                            }

                                            is AuthResult.Unauthorized -> {
                                                navController.popBackStack()
                                                navController.navigate(NavigationRoutes.Auth.RegisterPage)
                                            }
                                        }
                                    }
                                }
                                navigation<NavigationRoutes.Creators.CreatorsFolder>(
                                    startDestination = NavigationRoutes.Creators.CreatorsScreen
                                ) {
                                    composable<NavigationRoutes.Creators.CreatorsScreen> {
                                        val parentEntry =
                                            remember { navController.getBackStackEntry<NavigationRoutes.Creators.CreatorsFolder>() }
                                        val creatorsVM =
                                            viewModel<CreatorsScreenViewModel>(
                                                parentEntry,
                                                factory = RecipesApplication.appModule.creatorsScreenVMFactory
                                            )
                                        val state by creatorsVM.state.collectAsState()

                                        LaunchedEffect(Unit) {
                                            creatorsVM.onEvent(CreatorsScreenEvent.LoadData)
                                        }

                                        com.receipts.receipt_sharing.ui.creators.shared.CreatorsScreen(
                                            state = state,
                                            sharedTransitionScope = this@SharedTransitionLayout,
                                            animatedVisibility = this,
                                            onOpenMenu = {
                                                scope.launch {
                                                    drawerState.apply {
                                                        if (drawerState.isOpen) drawerState.close()
                                                        else drawerState.open()
                                                    }
                                                }
                                            },
                                            onGoToCreatorPage = {
                                                navController.navigate(
                                                    NavigationRoutes.Creators.CreatorPage(
                                                        it
                                                    )
                                                )
                                            },
                                            onEvent = creatorsVM::onEvent
                                        )
                                    }
                                    composable<NavigationRoutes.Creators.CreatorPage> {
                                        val parentEntry =
                                            remember { navController.getBackStackEntry<NavigationRoutes.Creators.CreatorsFolder>() }
                                        val args =
                                            it.toRoute<NavigationRoutes.Creators.CreatorPage>().creatorID
                                        val creatorVm =
                                            viewModel<CreatorPageViewModel>(
                                                parentEntry,
                                                factory = RecipesApplication.appModule.creatorPageVMFactory
                                            )
                                        LaunchedEffect(Unit) {
                                            creatorVm.onEvent(CreatorPageEvent.LoadCreator(args))
                                        }

                                        val state by creatorVm.state.collectAsState()

                                        com.receipts.receipt_sharing.ui.creators.shared.CreatorPage(
                                            state = state,
                                            sharedTransitionScope = this@SharedTransitionLayout,
                                            animatedVisibility = this,
                                            onGoBack = {
                                                navController.navigateUp()
                                            },
                                            onEvent = creatorVm::onEvent,
                                            onGoToCreatorRecipes = {
                                                navController.popBackStack()
                                                navController.navigate(
                                                    NavigationRoutes.Recipes.CreatorRecipesScreen(
                                                        it
                                                    )
                                                )
                                            },
                                            onReloadData = {
                                                creatorVm.onEvent(
                                                    CreatorPageEvent.LoadCreator(
                                                        args
                                                    )
                                                )
                                            },
                                            onGoToRecipe = {
                                                navController.navigate(
                                                    NavigationRoutes.Recipes.RecipePage(
                                                        it
                                                    )
                                                )
                                            }
                                        )
                                    }
                                    composable<NavigationRoutes.Creators.FollowsScreen> {
                                        val parentEntry =
                                            remember { navController.getBackStackEntry<NavigationRoutes.Creators.CreatorsFolder>() }
                                        val creatorsVm =
                                            viewModel<CreatorsScreenViewModel>(
                                                parentEntry,
                                                factory = RecipesApplication.appModule.creatorsScreenVMFactory
                                            )
                                        val state by creatorsVm.state.collectAsState()

                                        LaunchedEffect(Unit) {
                                            creatorsVm.onEvent(CreatorsScreenEvent.LoadFollows)
                                        }

                                        com.receipts.receipt_sharing.ui.creators.shared.CreatorsScreen(
                                            state = state,
                                            sharedTransitionScope = this@SharedTransitionLayout,
                                            animatedVisibility = this,
                                            onOpenMenu = {
                                                scope.launch {
                                                    drawerState.apply {
                                                        if (drawerState.isOpen) drawerState.close()
                                                        else drawerState.open()
                                                    }
                                                }
                                            },
                                            onEvent = creatorsVm::onEvent,
                                            onGoToCreatorPage = {
                                                navController.navigate(
                                                    NavigationRoutes.Creators.CreatorPage(
                                                        it
                                                    )
                                                )
                                            }
                                        )
                                    }
                                    composable<NavigationRoutes.Creators.UserInfoPage> {
                                        val parentEntry =
                                            remember { navController.getBackStackEntry<NavigationRoutes.Creators.CreatorsFolder>() }

                                        val profileVM = viewModel<ProfileViewModel>(
                                            parentEntry,
                                            factory = RecipesApplication.appModule.profilePageVMFactory
                                        )

                                        val state by profileVM.state.collectAsState()

                                        LaunchedEffect(Unit) {
                                            profileVM.onEvent(ProfilePageEvent.LoadUserInfo)
                                        }

                                        CreatorConfigPage(
                                            state = state,
                                            onEvent = profileVM::onEvent,
                                            onOpenMenu = {
                                                scope.launch {
                                                    drawerState.apply {
                                                        if (drawerState.isOpen)
                                                            drawerState.close()
                                                        else drawerState.open()
                                                    }
                                                }
                                            },
                                            onLogOut = {
                                                navController.popBackStack()
                                                navController.navigate(NavigationRoutes.Auth.AuthFolder)
                                            })
                                    }
                                    composable<NavigationRoutes.Creators.FollowersScreen> {
                                        val parentEntry =
                                            remember { navController.getBackStackEntry<NavigationRoutes.Creators.CreatorsFolder>() }
                                        val creatorsVM =
                                            viewModel<CreatorsScreenViewModel>(
                                                parentEntry,
                                                factory = RecipesApplication.appModule.creatorsScreenVMFactory
                                            )
                                        LaunchedEffect(Unit) {
                                            creatorsVM.onEvent(CreatorsScreenEvent.LoadFollowers())
                                        }

                                        val state by creatorsVM.state.collectAsState()
                                        com.receipts.receipt_sharing.ui.creators.shared.CreatorsScreen(
                                            state = state,
                                            sharedTransitionScope = this@SharedTransitionLayout,
                                            animatedVisibility = this,
                                            onOpenMenu = {
                                                scope.launch {
                                                    drawerState.apply {
                                                        if (drawerState.isOpen)
                                                            drawerState.close()
                                                        else drawerState.open()
                                                    }
                                                }
                                            },
                                            onGoToCreatorPage = {
                                                navController.navigate(
                                                    NavigationRoutes.Creators.CreatorPage(
                                                        it
                                                    )
                                                )
                                            },
                                            onEvent = creatorsVM::onEvent
                                        )
                                    }
                                }

                                navigation<NavigationRoutes.Recipes.RecipesFolder>(startDestination = NavigationRoutes.Recipes.RecipesScreen) {
                                    composable<NavigationRoutes.Recipes.CreatorRecipesScreen> {
                                        val args =
                                            it.toRoute<NavigationRoutes.Recipes.CreatorRecipesScreen>().creatorID
                                        val parentEntry =
                                            remember { navController.getBackStackEntry<NavigationRoutes.Recipes.RecipesFolder>() }
                                        val recipesScreenVm =
                                            viewModel<RecipesScreenViewModel>(
                                                parentEntry,
                                                factory = RecipesApplication.appModule.recipesScreenVMFactory
                                            )
                                        val state by recipesScreenVm.state.collectAsState()

                                        LaunchedEffect(Unit) {
                                            recipesScreenVm.onEvent(
                                                RecipesScreenEvent.LoadCreatorsRecipes(
                                                    args
                                                )
                                            )
                                        }

                                        com.receipts.receipt_sharing.ui.recipe.shared.RecipesScreen(
                                            state = state,
                                            sharedTransitionScope = this@SharedTransitionLayout,
                                            animatedVisibility = this,
                                            onEvent = recipesScreenVm::onEvent,
                                            onOpenMenu = {
                                                scope.launch {
                                                    drawerState.apply {
                                                        if (drawerState.isOpen) drawerState.close()
                                                        else drawerState.open()
                                                    }
                                                }
                                            },
                                            onGoToAddRecipe = {
                                                navController.navigate(NavigationRoutes.Recipes.RecipeAddingPage)
                                            },
                                            onGoToRecipe = {
                                                navController.navigate(
                                                    NavigationRoutes.Recipes.RecipePage(
                                                        it
                                                    )
                                                )
                                            },
                                            onGoToFilters = {
                                                navController.navigate(
                                                    NavigationRoutes.Recipes.FiltersSelection(
                                                        false
                                                    )
                                                )
                                            },
                                            onReloadPage = {
                                                recipesScreenVm.onEvent(
                                                    RecipesScreenEvent.LoadCreatorsRecipes(args)
                                                )
                                            }
                                        )
                                    }
                                    composable<NavigationRoutes.Recipes.FiltersSelection> {
                                        val isRecipePage =
                                            it.toRoute<NavigationRoutes.Recipes.FiltersSelection>().isRecipePage
                                        val parentEntry =
                                            remember { navController.getBackStackEntry<NavigationRoutes.Recipes.RecipesFolder>() }
                                        val recipesScreenVm =
                                            viewModel<RecipesScreenViewModel>(
                                                parentEntry,
                                                factory = RecipesApplication.appModule.recipesScreenVMFactory
                                            )
                                        val recipesPageVM =
                                            viewModel<RecipePageViewModel>(
                                                parentEntry,
                                                factory = RecipesApplication.appModule.recipePageVMFactory
                                            )
                                        LaunchedEffect(Unit) {
                                            recipesScreenVm.onEvent(RecipesScreenEvent.LoadFilters)
                                        }

                                        val state by recipesScreenVm.state.collectAsState()


                                        FiltersPage(categorizedItems = state.filters,
                                            onFiltersConfirmed = {
                                                if (isRecipePage)
                                                    recipesPageVM.onEvent(
                                                        RecipePageEvent.SetFilters(
                                                            it
                                                        )
                                                    )
                                                else
                                                    recipesScreenVm.onEvent(
                                                        RecipesScreenEvent.SetFilters(
                                                            it
                                                        )
                                                    )
                                                navController.navigateUp()
                                            },
                                            onCancelChanges = {
                                                navController.navigateUp()
                                            })
                                    }
                                    composable<NavigationRoutes.Recipes.RecipesScreen> {
                                        val parentEntry =
                                            remember { navController.getBackStackEntry<NavigationRoutes.Recipes.RecipesFolder>() }
                                        val recipesVM =
                                            viewModel<RecipesScreenViewModel>(
                                                parentEntry,
                                                factory = RecipesApplication.appModule.recipesScreenVMFactory
                                            )
                                        LaunchedEffect(Unit) {
                                            recipesVM.onEvent(RecipesScreenEvent.LoadData)
                                        }
                                        val state by recipesVM.state.collectAsState()


                                        com.receipts.receipt_sharing.ui.recipe.shared.RecipesScreen(
                                            state = state,
                                            sharedTransitionScope = this@SharedTransitionLayout,
                                            animatedVisibility = this,
                                            onEvent = recipesVM::onEvent,
                                            onOpenMenu = {
                                                scope.launch {
                                                    drawerState.apply {
                                                        if (drawerState.isOpen) drawerState.close()
                                                        else drawerState.open()
                                                    }
                                                }
                                            },
                                            onGoToAddRecipe = {
                                                navController.navigate(NavigationRoutes.Recipes.RecipeAddingPage)
                                            },
                                            onGoToRecipe = {
                                                navController.navigate(
                                                    NavigationRoutes.Recipes.RecipePage(
                                                        it
                                                    )
                                                )
                                            },
                                            onGoToFilters = {
                                                navController.navigate(
                                                    NavigationRoutes.Recipes.FiltersSelection(
                                                        false
                                                    )
                                                )
                                            },
                                            onReloadPage = {
                                                recipesVM.onEvent(RecipesScreenEvent.LoadData)
                                            }
                                        )
                                    }
                                    composable<NavigationRoutes.Recipes.OwnRecipesScreen> {
                                        val parentEntry =
                                            remember { navController.getBackStackEntry<NavigationRoutes.Recipes.RecipesFolder>() }
                                        val recipesVM =
                                            viewModel<RecipesScreenViewModel>(
                                                parentEntry,
                                                factory = RecipesApplication.appModule.recipesScreenVMFactory
                                            )
                                        val state by recipesVM.state.collectAsState()

                                        LaunchedEffect(Unit) {
                                            recipesVM.onEvent(RecipesScreenEvent.LoadOwnData)
                                        }

                                        com.receipts.receipt_sharing.ui.recipe.shared.RecipesScreen(
                                            state = state,
                                            onEvent = recipesVM::onEvent,
                                            sharedTransitionScope = this@SharedTransitionLayout,
                                            animatedVisibility = this,
                                            onOpenMenu = {
                                                scope.launch {
                                                    drawerState.apply {
                                                        if (drawerState.isOpen) drawerState.close()
                                                        else drawerState.open()
                                                    }
                                                }
                                            },
                                            onGoToAddRecipe = {
                                                navController.navigate(NavigationRoutes.Recipes.RecipeAddingPage)
                                            },
                                            onGoToRecipe = {
                                                navController.navigate(
                                                    NavigationRoutes.Recipes.RecipePage(
                                                        it
                                                    )
                                                )
                                            },
                                            onGoToFilters = {
                                                navController.navigate(
                                                    NavigationRoutes.Recipes.FiltersSelection(
                                                        false
                                                    )
                                                )
                                            },
                                            onReloadPage = {
                                                recipesVM.onEvent(RecipesScreenEvent.LoadOwnData)
                                            }
                                        )
                                    }
                                    composable<NavigationRoutes.Recipes.RecipePage> {
                                        val args =
                                            it.toRoute<NavigationRoutes.Recipes.RecipePage>().recipeID
                                        val parentEntry =
                                            remember { navController.getBackStackEntry<NavigationRoutes.Recipes.RecipesFolder>() }
                                        val recipePVM: RecipePageViewModel = viewModel(
                                            parentEntry,
                                            factory = RecipesApplication.appModule.recipePageVMFactory
                                        )
                                        LaunchedEffect(Unit) {
                                            recipePVM.onEvent(RecipePageEvent.LoadRecipe(args))
                                        }
                                        val state by recipePVM.state.collectAsState()


                                        com.receipts.receipt_sharing.ui.recipe.shared.RecipeConfigPage(
                                            sharedTransitionScope = this@SharedTransitionLayout,
                                            animatedVisibility = this,
                                            state = state,
                                            onEvent = recipePVM::onEvent,
                                            onGoBack = {
                                                navController.navigateUp()
                                            },
                                            onReloadData = {
                                                recipePVM.onEvent(
                                                    RecipePageEvent.LoadRecipe(
                                                        args
                                                    )
                                                )
                                            },
                                            onGoToPostReview = {
                                                navController.navigate(
                                                    NavigationRoutes.Recipes.ReviewPage(args)
                                                )
                                            },
                                            onGoToReviews = {
                                                navController.navigate(
                                                    NavigationRoutes.Recipes.ReviewsScreen(args)
                                                )
                                            },
                                            onGoToFilters = {
                                                navController.navigate(
                                                    NavigationRoutes.Recipes.FiltersSelection(true)
                                                )
                                            }
                                        )
                                    }
                                    composable<NavigationRoutes.Recipes.RecipeAddingPage> {
                                        val parentEntry =
                                            remember { navController.getBackStackEntry<NavigationRoutes.Recipes.RecipesFolder>() }
                                        val recipeVm =
                                            viewModel<RecipePageViewModel>(
                                                parentEntry,
                                                factory = RecipesApplication.appModule.recipePageVMFactory
                                            )
                                        LaunchedEffect(Unit) {
                                            recipeVm.onEvent(RecipePageEvent.ClearRecipeData)
                                        }
                                        val state by recipeVm.state.collectAsState()


                                        com.receipts.receipt_sharing.ui.recipe.shared.RecipeConfigPage(
                                            sharedTransitionScope = this@SharedTransitionLayout,
                                            animatedVisibility = this,
                                            state = state,
                                            onEvent = recipeVm::onEvent,
                                            onGoBack = {
                                                navController.navigateUp()
                                            },
                                            onReloadData = {
                                                recipeVm.onEvent(RecipePageEvent.ClearRecipeData)
                                            },
                                            onGoToPostReview = {
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    R.string.recipe_is_creating,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            },
                                            onGoToReviews = {
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    R.string.recipe_is_creating,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            },
                                            onGoToFilters = {
                                                navController.navigate(
                                                    NavigationRoutes.Recipes.FiltersSelection(true)
                                                )
                                            }
                                        )
                                    }
                                    composable<NavigationRoutes.Recipes.FavoritesPage> {
                                        val parentEntry =
                                            remember { navController.getBackStackEntry<NavigationRoutes.Recipes.RecipesFolder>() }
                                        val recipesVM =
                                            viewModel<RecipesScreenViewModel>(
                                                parentEntry,
                                                factory = RecipesApplication.appModule.recipesScreenVMFactory
                                            )
                                        LaunchedEffect(Unit) {
                                            recipesVM.onEvent(RecipesScreenEvent.LoadFavorites)
                                        }
                                        val state by recipesVM.state.collectAsState()

                                        com.receipts.receipt_sharing.ui.recipe.shared.RecipesScreen(
                                            state = state,
                                            sharedTransitionScope = this@SharedTransitionLayout,
                                            animatedVisibility = this,
                                            onEvent = recipesVM::onEvent,
                                            onOpenMenu = {
                                                scope.launch {
                                                    drawerState.apply {
                                                        if (drawerState.isOpen) drawerState.close()
                                                        else drawerState.open()
                                                    }
                                                }
                                            },
                                            onGoToAddRecipe = {
                                                navController.navigate(NavigationRoutes.Recipes.RecipeAddingPage)
                                            },
                                            onGoToRecipe = {
                                                navController.navigate(
                                                    NavigationRoutes.Recipes.RecipePage(
                                                        it
                                                    )
                                                )
                                            },
                                            onGoToFilters = {
                                                navController.navigate(
                                                    NavigationRoutes.Recipes.FiltersSelection(
                                                        false
                                                    )
                                                )
                                            },
                                            onReloadPage = { recipesVM.onEvent(RecipesScreenEvent.LoadFavorites) }
                                        )
                                    }
                                    composable<NavigationRoutes.Recipes.ReviewsScreen> {
                                        val args =
                                            it.toRoute<NavigationRoutes.Recipes.ReviewsScreen>()
                                        val parentEntry = remember {
                                            navController.getBackStackEntry<NavigationRoutes.Recipes.ReviewsScreen>()
                                        }
                                        val reviewsScreenVM = viewModel<ReviewsScreenViewModel>(
                                            viewModelStoreOwner = parentEntry,
                                            factory = RecipesApplication.appModule.reviewsScreenVMFactory
                                        )

                                        LaunchedEffect(Unit) {
                                            reviewsScreenVM.onEvent(
                                                ReviewsScreenEvent.LoadReviews(
                                                    args.recipeID
                                                )
                                            )
                                        }

                                        val state by reviewsScreenVM.state.collectAsState()

                                        ReviewsScreen(
                                            state = state,
                                            onEvent = reviewsScreenVM::onEvent,
                                            onGoBack = { navController.navigateUp() },
                                            onReloadPage = {
                                                reviewsScreenVM.onEvent(
                                                    ReviewsScreenEvent.LoadReviews(
                                                        args.recipeID
                                                    )
                                                )
                                            },
                                            onEditClick = {
                                                navController.navigate(
                                                    NavigationRoutes.Recipes.ReviewPage(
                                                        args.recipeID
                                                    )
                                                )
                                            }
                                        )
                                    }
                                    composable<NavigationRoutes.Recipes.ReviewPage> {
                                        val args = it.toRoute<NavigationRoutes.Recipes.ReviewPage>()
                                        val parentEntry = remember {
                                            navController.getBackStackEntry<NavigationRoutes.Recipes.ReviewPage>()
                                        }
                                        val reviewPageVM = viewModel<ReviewPageViewModel>(
                                            viewModelStoreOwner = parentEntry,
                                            factory = RecipesApplication.appModule.reviewPageVMFactory
                                        )
                                        LaunchedEffect(Unit) {
                                            reviewPageVM.onEvent(
                                                ReviewPageEvent.LoadReviewByRecipe(
                                                    args.recipeID
                                                )
                                            )
                                        }

                                        val state by reviewPageVM.state.collectAsState()
                                        ReviewPage(
                                            state = state,
                                            onEvent = reviewPageVM::onEvent,
                                            onGoBack = { navController.navigateUp() },
                                            onRefresh = {
                                                reviewPageVM.onEvent(
                                                    ReviewPageEvent.LoadReviewByRecipe(
                                                        args.recipeID
                                                    )
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
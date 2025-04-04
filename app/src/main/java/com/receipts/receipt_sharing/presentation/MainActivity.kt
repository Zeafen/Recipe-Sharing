package com.receipts.receipt_sharing.presentation

import NavigationRoutes
import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
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
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.data.dataStore.UserInfo
import com.receipts.receipt_sharing.data.helpers.UnsafeImageLoader
import com.receipts.receipt_sharing.data.repositoriesImpl.authDataStore
import com.receipts.receipt_sharing.domain.response.AuthResult
import com.receipts.receipt_sharing.presentation.auth.AuthEvent
import com.receipts.receipt_sharing.presentation.auth.AuthViewModel
import com.receipts.receipt_sharing.presentation.creators.creatorPage.CreatorPageEvent
import com.receipts.receipt_sharing.presentation.creators.creatorPage.CreatorPageViewModel
import com.receipts.receipt_sharing.presentation.creators.creatorsScreen.CreatorLoadDataType
import com.receipts.receipt_sharing.presentation.creators.creatorsScreen.CreatorsScreenEvent
import com.receipts.receipt_sharing.presentation.creators.creatorsScreen.CreatorsScreenViewModel
import com.receipts.receipt_sharing.presentation.creators.profile.ProfilePageEvent
import com.receipts.receipt_sharing.presentation.creators.profile.ProfileViewModel
import com.receipts.receipt_sharing.presentation.home.HomePageEvent
import com.receipts.receipt_sharing.presentation.home.HomePageViewModel
import com.receipts.receipt_sharing.presentation.recipes.recipePage.RecipePageEvent
import com.receipts.receipt_sharing.presentation.recipes.recipePage.RecipePageViewModel
import com.receipts.receipt_sharing.presentation.recipes.recipesScreen.RecipesLoadedDataType
import com.receipts.receipt_sharing.presentation.recipes.recipesScreen.RecipesScreenEvent
import com.receipts.receipt_sharing.presentation.recipes.recipesScreen.RecipesScreenViewModel
import com.receipts.receipt_sharing.presentation.reviews.reviewPage.ReviewPageEvent
import com.receipts.receipt_sharing.presentation.reviews.reviewPage.ReviewPageViewModel
import com.receipts.receipt_sharing.presentation.reviews.reviewsScreen.ReviewsScreenEvent
import com.receipts.receipt_sharing.presentation.reviews.reviewsScreen.ReviewsScreenViewModel
import com.receipts.receipt_sharing.ui.auth.ForgotPasswordPage
import com.receipts.receipt_sharing.ui.auth.LoginScreen
import com.receipts.receipt_sharing.ui.auth.RegisterScreen
import com.receipts.receipt_sharing.ui.creators.profile.CreatorConfigPage
import com.receipts.receipt_sharing.ui.home.HomePage
import com.receipts.receipt_sharing.ui.infoPages.ErrorInfoPage
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
        val navItems = listOf(
            RecipeNavigationItem(
                NavigationRoutes.Recipes.RecipesFolder,
                R.string.home_page_title,
                R.drawable.home_ic
            ),
            RecipeNavigationItem(
                NavigationRoutes.Recipes.RecipesScreen,
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
                R.string.creator_screen_header,
                R.drawable.creators_page_ic
            ),
            RecipeNavigationItem(
                NavigationRoutes.Creators.FollowsScreen,
                R.string.follows_page_title,
                R.drawable.follows_page_ic
            ),
            RecipeNavigationItem(
                NavigationRoutes.Creators.ProfilePage,
                R.string.profile_page_header,
                R.drawable.user_info_page_ic
            )
        )


        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            val userState by context.authDataStore.data.collectAsState(initial = UserInfo())

            RecipeSharing_theme {
                Surface {
                    ModalNavigationDrawer(modifier = Modifier,
                        drawerState = drawerState,
                        drawerContent = {
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
                                        val authVM = viewModel<AuthViewModel>(
                                            viewModelStoreOwner = parentEntry,
                                            factory = RecipesApplication.appModule.authVMFactory
                                        )

                                        LaunchedEffect(Unit) {
                                            authVM.onEvent(AuthEvent.ClearData)
                                        }

                                        val state by authVM.state.collectAsState()
                                        RegisterScreen(
                                            onGotoLogin = {
                                                navController.navigate(NavigationRoutes.Auth.AuthorizePage)
                                            },
                                            state = state,
                                            onEvent = authVM::onEvent,
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
                                        val authVM =
                                            viewModel<AuthViewModel>(
                                                parentEntry,
                                                factory = RecipesApplication.appModule.authVMFactory
                                            )
                                        LaunchedEffect(Unit) {
                                            authVM.onEvent(AuthEvent.ClearData)
                                            Log.i("composing", "Composing authorizePage")
                                        }
                                        val state by authVM.state.collectAsState()

                                        LoginScreen(
                                            onGotoRegister = {
                                                navController.navigate(
                                                    NavigationRoutes.Auth.RegisterPage
                                                )
                                            },
                                            state = state,
                                            onEvent = authVM::onEvent,
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

                                        ForgotPasswordPage(
                                            state = state,
                                            onEvent = viewModel::onEvent,
                                            onGoBackClick = { navController.navigateUp() }
                                        )
                                    }
                                    composable<NavigationRoutes.Auth.TryAuthorizePage> {
                                        val parentEntry = remember {
                                            navController.getBackStackEntry<NavigationRoutes.Auth.AuthFolder>()
                                        }
                                        val viewModel: AuthViewModel = viewModel(
                                            viewModelStoreOwner = parentEntry,
                                            factory = RecipesApplication.appModule.authVMFactory
                                        )
                                        val state by viewModel.state.collectAsState()
                                        LaunchedEffect(Unit) {
                                            viewModel.onEvent(AuthEvent.Authorize)
                                        }
                                        LaunchedEffect(state.result.info) {
                                            if (!state.result.info.isNullOrEmpty())
                                                Toast.makeText(
                                                    context,
                                                    state.result.info,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                        }

                                        LaunchedEffect(state.result) {
                                            when (state.result) {
                                                is AuthResult.Authorized -> {
                                                    navController.popBackStack()
                                                    if (userState.lastSelectedPageInd > 0)
                                                        navController.navigate(navItems[userState.lastSelectedPageInd].route)
                                                    else
                                                        navController.navigate(NavigationRoutes.Recipes.RecipesFolder)
                                                }

                                                is AuthResult.Unauthorized -> {
                                                    navController.popBackStack()
                                                    navController.navigate(NavigationRoutes.Auth.RegisterPage)
                                                }

                                                else -> {}
                                            }
                                        }

                                        if (state.result is AuthResult.Loading) {
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
                                        } else if (state.result is AuthResult.Error)
                                            ErrorInfoPage(
                                                errorInfo = state.result.info
                                                    ?: stringResource(R.string.unknown_error_txt),
                                                onReloadPage = { viewModel.onEvent(AuthEvent.Authorize) }
                                            )
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
                                            creatorsVM.onEvent(
                                                CreatorsScreenEvent.SetLoadDataType(
                                                    CreatorLoadDataType.All
                                                )
                                            )
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
                                            onEvent = creatorsVM::onEvent,
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
                                            creatorsVm.onEvent(
                                                CreatorsScreenEvent.SetLoadDataType(
                                                    CreatorLoadDataType.Follows()
                                                )
                                            )
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
                                            },
                                        )
                                    }
                                    composable<NavigationRoutes.Creators.ProfilePage> {
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
                                            modifier = Modifier
                                                .windowInsetsPadding(WindowInsets.systemBars),
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
                                                navController.navigate(NavigationRoutes.Auth.RegisterPage)
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
                                            creatorsVM.onEvent(
                                                CreatorsScreenEvent.SetLoadDataType(
                                                    CreatorLoadDataType.Followers()
                                                )
                                            )
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
                                            onEvent = creatorsVM::onEvent,
                                        )
                                    }
                                }

                                navigation<NavigationRoutes.Recipes.RecipesFolder>(startDestination = NavigationRoutes.Recipes.HomePage) {
                                    composable<NavigationRoutes.Recipes.HomePage> {
                                        val parentEntry = remember {
                                            navController.getBackStackEntry<NavigationRoutes.Recipes.RecipesFolder>()
                                        }
                                        val viewModel = viewModel<HomePageViewModel>(
                                            viewModelStoreOwner = parentEntry,
                                            factory = RecipesApplication.appModule.homePageVMFactory
                                        )
                                        LaunchedEffect(Unit) {
                                            viewModel.onEvent(HomePageEvent.LoadData)
                                        }
                                        val state by viewModel.state.collectAsState()

                                        HomePage(
                                            state = state,
                                            onEvent = viewModel::onEvent,
                                            onGoToCreator = {
                                                navController.navigate(
                                                    NavigationRoutes.Creators.CreatorPage(
                                                        it
                                                    )
                                                )
                                            },
                                            onGoToCreators = {
                                                navController.navigate(
                                                    NavigationRoutes.Creators.CreatorsScreen
                                                )
                                            },
                                            onGoToRecipe = {
                                                navController.navigate(
                                                    NavigationRoutes.Recipes.RecipePage(
                                                        it
                                                    )
                                                )
                                            },
                                            onGoToRecipes = {
                                                navController.navigate(
                                                    NavigationRoutes.Recipes.RecipesScreen
                                                )
                                            },
                                            onOpenMenu = {
                                                scope.launch {
                                                    if (!drawerState.isAnimationRunning) {
                                                        if (drawerState.isOpen)
                                                            drawerState.close()
                                                        else drawerState.open()
                                                    }
                                                }
                                            },
                                            onGoToProfile = {
                                                navController.navigate(NavigationRoutes.Creators.ProfilePage)
                                            }
                                        )
                                    }
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
                                                RecipesScreenEvent.SetLoadDataType(
                                                    RecipesLoadedDataType.CreatorRecipes(args)
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
                                            onGoToRecipe = {
                                                navController.navigate(
                                                    NavigationRoutes.Recipes.RecipePage(
                                                        it
                                                    )
                                                )
                                            },
                                            onGoToAddRecipe = {
                                                navController.navigate(
                                                    NavigationRoutes.Recipes.RecipePage(null)
                                                )
                                            }
                                        )
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
                                            recipesVM.onEvent(
                                                RecipesScreenEvent.SetLoadDataType(
                                                    RecipesLoadedDataType.All
                                                )
                                            )
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
                                            onGoToRecipe = {
                                                navController.navigate(
                                                    NavigationRoutes.Recipes.RecipePage(
                                                        it
                                                    )
                                                )
                                            },
                                            onGoToAddRecipe = {
                                                navController.navigate(
                                                    NavigationRoutes.Recipes.RecipePage(null)
                                                )
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
                                            recipesVM.onEvent(
                                                RecipesScreenEvent.SetLoadDataType(
                                                    RecipesLoadedDataType.OwnDataRecipes
                                                )
                                            )
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
                                            onGoToRecipe = {
                                                navController.navigate(
                                                    NavigationRoutes.Recipes.RecipePage(
                                                        it
                                                    )
                                                )
                                            },
                                            onGoToAddRecipe = {
                                                navController.navigate(
                                                    NavigationRoutes.Recipes.RecipePage(null)
                                                )
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
                                            if (args.isNullOrEmpty())
                                                recipePVM.onEvent(RecipePageEvent.InitializeRecipe)
                                            else recipePVM.onEvent(RecipePageEvent.LoadRecipe(args))
                                        }
                                        val state by recipePVM.state.collectAsState()


                                        com.receipts.receipt_sharing.ui.recipe.shared.RecipeConfigPage(
                                            modifier = Modifier
                                                .windowInsetsPadding(WindowInsets.systemBars),
                                            sharedTransitionScope = this@SharedTransitionLayout,
                                            animatedVisibility = this,
                                            state = state,
                                            onEvent = recipePVM::onEvent,
                                            onGoBack = {
                                                navController.navigateUp()
                                            },
                                            onReloadData = {
                                                args?.let {
                                                    recipePVM.onEvent(
                                                        RecipePageEvent.LoadRecipe(
                                                            it
                                                        )
                                                    )
                                                } ?: if(!state.recipe.data?.recipeID.isNullOrEmpty()){
                                                    RecipePageEvent.LoadRecipe(state.recipe.data!!.recipeID)
                                                }
                                                else {
                                                    RecipePageEvent.InitializeRecipe
                                                }
                                            },
                                            onGoToPostReview = {
                                                args?.let {
                                                    navController.navigate(
                                                        NavigationRoutes.Recipes.ReviewPage(args)
                                                    )
                                                }
                                            },
                                            onGoToReviews = {
                                                args?.let {
                                                    navController.navigate(
                                                        NavigationRoutes.Recipes.ReviewsScreen(args)
                                                    )
                                                }
                                            },
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
                                            recipesVM.onEvent(
                                                RecipesScreenEvent.SetLoadDataType(
                                                    RecipesLoadedDataType.Favorites
                                                )
                                            )
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
                                            onGoToRecipe = {
                                                navController.navigate(
                                                    NavigationRoutes.Recipes.RecipePage(
                                                        it
                                                    )
                                                )
                                            },
                                            onGoToAddRecipe = {
                                                navController.navigate(
                                                    NavigationRoutes.Recipes.RecipePage(null)
                                                )
                                            }
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
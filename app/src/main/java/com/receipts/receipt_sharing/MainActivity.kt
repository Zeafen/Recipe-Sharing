package com.receipts.receipt_sharing

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.receipts.receipt_sharing.data.dataStore.UserInfo
import com.receipts.receipt_sharing.data.repositories.authDataStore
import com.receipts.receipt_sharing.data.response.AuthResult
import com.receipts.receipt_sharing.domain.AnnouncementWorker
import com.receipts.receipt_sharing.domain.helpers.isPermissionsGranted
import com.receipts.receipt_sharing.domain.viewModels.AuthViewModel
import com.receipts.receipt_sharing.domain.viewModels.CreatorPageEvent
import com.receipts.receipt_sharing.domain.viewModels.CreatorPageViewModel
import com.receipts.receipt_sharing.domain.viewModels.CreatorsScreenEvent
import com.receipts.receipt_sharing.domain.viewModels.CreatorsScreenViewModel
import com.receipts.receipt_sharing.domain.viewModels.RecipePageEvent
import com.receipts.receipt_sharing.domain.viewModels.RecipePageViewModel
import com.receipts.receipt_sharing.domain.viewModels.RecipesScreenEvent
import com.receipts.receipt_sharing.domain.viewModels.RecipesScreenViewModel
import com.receipts.receipt_sharing.ui.auth.LoginScreen
import com.receipts.receipt_sharing.ui.auth.RegisterScreen
import com.receipts.receipt_sharing.ui.creators.CreatorConfigPage
import com.receipts.receipt_sharing.ui.creators.CreatorPage
import com.receipts.receipt_sharing.ui.creators.CreatorsScreen
import com.receipts.receipt_sharing.ui.filters.FiltersPage
import com.receipts.receipt_sharing.ui.recipe.RecipeConfigPage
import com.receipts.receipt_sharing.ui.recipe.RecipePage
import com.receipts.receipt_sharing.ui.recipe.RecipesScreen
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.util.concurrent.TimeUnit

data class RecipeNavigationItem(
    val route : NavigationRoutes,
    val nameID : Int,
    val iconID : Int)


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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
            val userState by context.authDataStore.data.collectAsState(initial = UserInfo())
            var selectedPage by rememberSaveable {
                androidx.compose.runtime.mutableIntStateOf(-1)
            }
            val navItems = listOf(
                RecipeNavigationItem(
                    NavigationRoutes.Recipes.RecipesFolder,
                    R.string.recipes_page_title,
                    R.drawable.recipes_page_ic
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
                                    Image(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        painter = painterResource(id = R.drawable.no_image),
                                        contentDescription = "",
                                        contentScale = ContentScale.FillWidth
                                    )
                                    Text(
                                        modifier = Modifier
                                            .padding(start = 16.dp, top = 8.dp, bottom = 32.dp),
                                        text = "User name",
                                        style = MaterialTheme.typography.headlineLarge
                                    )
                                    Divider()

                                    navItems.indices.forEach {
                                        NavigationDrawerItem(
                                            label = { Text(text = stringResource(id = navItems[it].nameID)) },
                                            icon = {
                                                Icon(
                                                    painterResource(id = navItems[it].iconID),
                                                    contentDescription = ""
                                                )
                                            },
                                            selected = selectedPage == it,
                                            onClick = {
                                                try {
                                                    navController.popBackStack()
                                                    navController.navigate(navItems[it].route)
                                                    selectedPage = it
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
                                        Divider()
                                    }

                                }
                            }
                        }
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = NavigationRoutes.Auth.AuthFolder
                        ) {
                            navigation<NavigationRoutes.Auth.AuthFolder>(
                                startDestination = NavigationRoutes.Auth.TryAuthorizePage)
                            {
                                composable<NavigationRoutes.Auth.RegisterPage> {
                                    val parentEntry =
                                        remember { navController.getBackStackEntry<NavigationRoutes.Auth.AuthFolder>() }
                                    val VM =
                                        hiltViewModel<AuthViewModel>(parentEntry)
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
                                        hiltViewModel<AuthViewModel>(parentEntry)
                                    val state by authVm.state.collectAsState()

                                    LoginScreen(
                                        onGotoRegister = { navController.navigate(NavigationRoutes.Auth.RegisterPage) },
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
                                        })
                                }
                                composable<NavigationRoutes.Auth.TryAuthorizePage> {
                                    val parentEntry =
                                        remember { navController.getBackStackEntry<NavigationRoutes.Auth.AuthFolder>() }
                                    val authVm =
                                        hiltViewModel<AuthViewModel>(parentEntry)
                                    val state by authVm.state.collectAsState()

                                    when(state.result){
                                        is AuthResult.Authorized -> {
                                            navController.popBackStack()
                                            navController.navigate(NavigationRoutes.Recipes.RecipesFolder)
                                        }
                                        is AuthResult.Error -> {
                                            navController.popBackStack()
                                            navController.navigate(NavigationRoutes.Auth.RegisterPage)
                                            Toast.makeText(context, R.string.token_expired, Toast.LENGTH_SHORT).show()
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
                            navigation<NavigationRoutes.Creators.CreatorsFolder>(startDestination = NavigationRoutes.Creators.CreatorsScreen) {
                                composable<NavigationRoutes.Creators.CreatorsScreen> {
                                    val parentEntry =
                                        remember { navController.getBackStackEntry<NavigationRoutes.Creators.CreatorsFolder>() }
                                    val creatorsVM =
                                        hiltViewModel<CreatorsScreenViewModel>(parentEntry)
                                    val creatorVM = hiltViewModel<CreatorPageViewModel>(parentEntry)
                                    val state by creatorsVM.state.collectAsState()

                                    if(state.followsLoaded)
                                        creatorsVM.onEvent(CreatorsScreenEvent.LoadData)

                                    CreatorsScreen(
                                        state = state,
                                        onOpenMenu = {
                                            scope.launch {
                                                drawerState.apply {
                                                    if (drawerState.isOpen) drawerState.close()
                                                    else drawerState.open()
                                                }
                                            }
                                        },
                                        onGoToCreatorPage = {
                                            creatorVM.onEvent(CreatorPageEvent.LoadCreator(it))
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
                                        hiltViewModel<CreatorPageViewModel>(parentEntry)
                                    val state by creatorVm.state.collectAsState()

                                    CreatorPage(
                                        state = state,
                                        onOpenMenu = {
                                            scope.launch {
                                                drawerState.apply {
                                                    if (drawerState.isOpen) drawerState.close()
                                                    else drawerState.open()
                                                }
                                            }
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
                                        }
                                    )
                                }
                                composable<NavigationRoutes.Creators.FollowsScreen> {
                                    val parentEntry =
                                        remember { navController.getBackStackEntry<NavigationRoutes.Creators.CreatorsFolder>() }
                                    val creatorVM = hiltViewModel<CreatorPageViewModel>(parentEntry)
                                    val creatorsVm =
                                        hiltViewModel<CreatorsScreenViewModel>(parentEntry)
                                    val state by creatorsVm.state.collectAsState()

                                    if(!state.followsLoaded)
                                        creatorsVm.onEvent(CreatorsScreenEvent.LoadFollows)

                                    CreatorsScreen(
                                        state = state,
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
                                            creatorVM.onEvent(CreatorPageEvent.LoadCreator(it))
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

                                    val creatorVM = hiltViewModel<CreatorPageViewModel>(parentEntry)
                                    val creatorsVM =
                                        hiltViewModel<CreatorsScreenViewModel>(parentEntry)

                                    val state by creatorVM.state.collectAsState()

                                    if (state.creatorName != userState.userName)
                                        creatorVM.onEvent(CreatorPageEvent.LoadUserInfo)

                                    CreatorConfigPage(
                                        state = state,
                                        onEvent = creatorVM::onEvent,
                                        onOpenMenu = {
                                            scope.launch {
                                                drawerState.apply {
                                                    if (drawerState.isOpen)
                                                        drawerState.close()
                                                    else drawerState.open()
                                                }
                                            }
                                        },
                                        onGoToAddRecipePage = {
                                            navController.popBackStack()
                                            navController.navigate(NavigationRoutes.Recipes.RecipeAddingPage)
                                        },
                                        onGoToFollowers = {
                                            creatorsVM.onEvent(CreatorsScreenEvent.LoadFollowers())
                                            navController.navigate(NavigationRoutes.Creators.FollowersScreen)
                                        })
                                }
                                composable<NavigationRoutes.Creators.FollowersScreen> {
                                    val parentEntry =
                                        remember { navController.getBackStackEntry<NavigationRoutes.Creators.CreatorsFolder>() }
                                    val creatorsVM =
                                        hiltViewModel<CreatorsScreenViewModel>(parentEntry)
                                    val creatorVM = hiltViewModel<CreatorPageViewModel>(parentEntry)
                                    val state by creatorsVM.state.collectAsState()
                                    CreatorsScreen(
                                        state = state,
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
                                            creatorVM.onEvent(CreatorPageEvent.LoadCreator(it))
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
                                    val args = it.toRoute<NavigationRoutes.Recipes.CreatorRecipesScreen>().creatorID
                                    val parentEntry =
                                        remember { navController.getBackStackEntry<NavigationRoutes.Recipes.RecipesFolder>() }
                                    val recipesScreenVm =
                                        hiltViewModel<RecipesScreenViewModel>(parentEntry)
                                    val recipeVM =
                                        hiltViewModel<RecipePageViewModel>(parentEntry)
                                    val state by recipesScreenVm.state.collectAsState()
                                    if(!state.creatorLoaded)
                                        recipesScreenVm.onEvent(RecipesScreenEvent.LoadCreatorsRecipes(args))

                                    RecipesScreen(
                                        state = state,
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
                                            recipeVM.onEvent(RecipePageEvent.ClearData)
                                            navController.navigate(NavigationRoutes.Recipes.RecipeAddingPage)
                                        },
                                        onGoToRecipe = {
                                            recipeVM.onEvent(RecipePageEvent.LoadRecipe(it))
                                            navController.navigate(NavigationRoutes.Recipes.RecipePage(it))
                                        },
                                        onGoToFilters = {
                                            recipesScreenVm.onEvent(RecipesScreenEvent.LoadFilters)
                                            navController.navigate(NavigationRoutes.Recipes.FiltersSelection)
                                        }
                                    )


                                }

                                composable<NavigationRoutes.Recipes.FiltersSelection> {
                                    val parentEntry =
                                        remember { navController.getBackStackEntry<NavigationRoutes.Recipes.RecipesFolder>() }
                                    val recipesScreenVm =
                                        hiltViewModel<RecipesScreenViewModel>(parentEntry)
                                    val state by recipesScreenVm.state.collectAsState()

                                    FiltersPage(categorizedItems = state.filters,
                                        onFiltersConfirmed = {
                                            recipesScreenVm.onEvent(
                                                RecipesScreenEvent.SetFilters(
                                                    it
                                                )
                                            )
                                            navController.navigateUp()
                                        },
                                        onCancelChanges = {
                                            navController.navigateUp() })
                                }
                                composable<NavigationRoutes.Recipes.RecipesScreen> {
                                    val parentEntry =
                                        remember { navController.getBackStackEntry<NavigationRoutes.Recipes.RecipesFolder>() }
                                    val recipePVM: RecipePageViewModel = hiltViewModel(parentEntry)
                                    val recipesVM =
                                        hiltViewModel<RecipesScreenViewModel>(parentEntry)
                                    val state by recipesVM.state.collectAsState()
                                    RecipesScreen(
                                        state = state,
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
                                            recipePVM.onEvent(RecipePageEvent.ClearData)
                                            navController.navigate(NavigationRoutes.Recipes.RecipeAddingPage)
                                        },
                                        onGoToRecipe = {
                                            recipePVM.onEvent(RecipePageEvent.LoadRecipe(it))
                                            navController.navigate(
                                                NavigationRoutes.Recipes.RecipePage(
                                                    it
                                                )
                                            )
                                        },
                                        onGoToFilters = {
                                            recipesVM.onEvent(RecipesScreenEvent.LoadFilters)
                                            navController.navigate(NavigationRoutes.Recipes.FiltersSelection)
                                        }
                                    )
                                }
                                composable<NavigationRoutes.Recipes.RecipePage> {
                                    val args =
                                        it.toRoute<NavigationRoutes.Recipes.RecipePage>().recipeID
                                    val parentEntry =
                                        remember { navController.getBackStackEntry<NavigationRoutes.Recipes.RecipesFolder>() }
                                    val recipePVM: RecipePageViewModel = hiltViewModel(parentEntry)
                                    val recipesScreenVM =
                                        hiltViewModel<RecipesScreenViewModel>(parentEntry)
                                    val state by recipePVM.state.collectAsState()
                                    when (state.own) {
                                        true -> RecipeConfigPage(
                                            state = state,
                                            onEvent = recipePVM::onEvent,
                                            onOpenMenu = {
                                                scope.launch {
                                                    drawerState.apply {
                                                        if (drawerState.isOpen) drawerState.close()
                                                        else drawerState.open()
                                                    }
                                                }
                                            },
                                            onReloadData = {
                                                recipePVM.onEvent(
                                                    RecipePageEvent.LoadRecipe(
                                                        args
                                                    )
                                                )
                                            },
                                            onConfigCompleted = {
                                                recipesScreenVM.onEvent(RecipesScreenEvent.LoadData)
                                                navController.navigate(NavigationRoutes.Recipes.RecipesScreen)
                                            })

                                        false -> RecipePage(
                                            state = state,
                                            onOpenMenu = {
                                                scope.launch {
                                                    drawerState.apply {
                                                        if (drawerState.isOpen) drawerState.close()
                                                        else drawerState.open()
                                                    }
                                                }
                                            },
                                            onEvent = recipePVM::onEvent,
                                            onReloadData = {
                                                recipePVM.onEvent(
                                                    RecipePageEvent.LoadRecipe(
                                                        args
                                                    )
                                                )
                                            },
                                            onGoToFilteredScreen = {
                                                recipesScreenVM.onEvent(
                                                    RecipesScreenEvent.SetFilters(
                                                        listOf(it)
                                                    )
                                                )
                                                recipesScreenVM.onEvent(RecipesScreenEvent.LoadData)
                                                navController.navigate(NavigationRoutes.Recipes.RecipesScreen)
                                            }
                                        )
                                    }
                                }
                                composable<NavigationRoutes.Recipes.RecipeAddingPage> {
                                    val parentEntry =
                                        remember { navController.getBackStackEntry<NavigationRoutes.Recipes.RecipesFolder>() }
                                    val recipeVm =
                                        hiltViewModel<RecipePageViewModel>(parentEntry)
                                    val recipesScreenVM =
                                        hiltViewModel<RecipesScreenViewModel>(parentEntry)
                                    val state by recipeVm.state.collectAsState()
                                    RecipeConfigPage(modifier = Modifier.fillMaxSize(),
                                        state = state,
                                        onEvent = recipeVm::onEvent,
                                        onOpenMenu = {
                                            scope.launch {
                                                drawerState.apply {
                                                    if (drawerState.isOpen) drawerState.close()
                                                    else drawerState.open()
                                                }
                                            }
                                        },
                                        onReloadData = { recipeVm.onEvent(RecipePageEvent.ClearData) },
                                        onConfigCompleted = {
                                            recipesScreenVM.onEvent(RecipesScreenEvent.LoadData)
                                            navController.navigate(NavigationRoutes.Recipes.RecipesScreen)
                                        })
                                }
                                composable<NavigationRoutes.Recipes.FavoritesPage> {
                                    val parentEntry =
                                        remember { navController.getBackStackEntry<NavigationRoutes.Recipes.RecipesFolder>() }
                                    val recipesVM =
                                        hiltViewModel<RecipesScreenViewModel>(parentEntry)
                                    val recipeVM =
                                        hiltViewModel<RecipePageViewModel>(parentEntry)
                                    val state by recipesVM.state.collectAsState()
                                    if (!state.favoritesLoaded)
                                        recipesVM.onEvent(RecipesScreenEvent.LoadFavorites)

                                    RecipesScreen(
                                        state = state,
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
                                            recipeVM.onEvent(RecipePageEvent.ClearData)
                                            navController.navigate(NavigationRoutes.Recipes.RecipeAddingPage)
                                        },
                                        onGoToRecipe = {
                                            recipeVM.onEvent(RecipePageEvent.LoadRecipe(it))
                                            navController.navigate(
                                                NavigationRoutes.Recipes.RecipePage(
                                                    it
                                                )
                                            )
                                        },
                                        onGoToFilters = {
                                            recipesVM.onEvent(RecipesScreenEvent.LoadFilters)
                                            navController.navigate(NavigationRoutes.Recipes.FiltersSelection)
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
    private fun startWorker() {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val workRequest = PeriodicWorkRequestBuilder<AnnouncementWorker>(2, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()
        if (isPermissionsGranted(
                applicationContext,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    listOf(
                        Manifest.permission.POST_NOTIFICATIONS,
                        Manifest.permission.INTERNET
                    )
                else listOf(Manifest.permission.INTERNET)
            )) {
                WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                    AnnouncementWorker.WORK_NAME,
                    ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                    workRequest
                )
            }
        else  WorkManager.getInstance(applicationContext).cancelUniqueWork(AnnouncementWorker.WORK_NAME)
    }
}



sealed class NavigationRoutes{

    sealed class Auth : NavigationRoutes() {
        @Serializable
        data object AuthFolder : Auth()

        @Serializable
        data object RegisterPage : Auth()

        @Serializable
        data object AuthorizePage : Auth()

        @Serializable
        data object TryAuthorizePage : Auth()
    }

    sealed class Creators : NavigationRoutes(){
        @Serializable
        data object CreatorsFolder : Creators()

        @Serializable
        data object CreatorsScreen : Creators()

        @Serializable
        data object FollowsScreen : Creators()

        @Serializable
        data object FollowersScreen : Creators()

        @Serializable
        data object UserInfoPage : Creators()

        @Serializable
        data class CreatorPage(val creatorID : String) : Creators()
    }
    sealed class Recipes : NavigationRoutes(){
        @Serializable
        data object RecipesFolder : Recipes()

        @Serializable
        data object FavoritesPage : Recipes()

        @Serializable
        data object RecipesScreen : Recipes()

        @Serializable
        data object FiltersSelection : Recipes()

        @Serializable
        data class CreatorRecipesScreen(val creatorID : String) : Recipes()

        @Serializable
        data class RecipePage(val recipeID : String) : Recipes()

        @Serializable
        data object RecipeAddingPage : Recipes()
    }
}
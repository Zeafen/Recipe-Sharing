package com.receipts.receipt_sharing.ui.recipe.shared

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.response.ApiResult
import com.receipts.receipt_sharing.presentation.CellsAmount
import com.receipts.receipt_sharing.presentation.RecipeSharedElementKey
import com.receipts.receipt_sharing.presentation.RecipeSharedElementType
import com.receipts.receipt_sharing.presentation.recipes.recipePage.RecipePageEvent
import com.receipts.receipt_sharing.presentation.recipes.recipePage.RecipePageState
import com.receipts.receipt_sharing.presentation.recipes.recipesScreen.RecipesLoadedDataType
import com.receipts.receipt_sharing.presentation.recipes.recipesScreen.RecipesScreenEvent
import com.receipts.receipt_sharing.presentation.recipes.recipesScreen.RecipesScreenState
import com.receipts.receipt_sharing.ui.PageSelectionRow
import com.receipts.receipt_sharing.ui.TwoLayerTopAppBar
import com.receipts.receipt_sharing.ui.effects.shimmerEffect
import com.receipts.receipt_sharing.ui.filters.FiltersPage
import com.receipts.receipt_sharing.ui.infoPages.ErrorInfoPage
import com.receipts.receipt_sharing.ui.recipe.elements.FiltersTab
import com.receipts.receipt_sharing.ui.recipe.elements.RecipeOrderingDropDownMenu
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme

/**
 * Composes recipes screen
 * @param modifier Modifier applied to recipes screen
 * @param state state object used to control layout
 * @param onGoToRecipe called when user clicks on recipe card
 * @param onGoToAddRecipe called when user clicks on "Add" button
 * @param onEvent called when user interacts with ui
 * @param onOpenMenu called when user clicks "Menu" navigation button
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun RecipesScreen(
    modifier: Modifier = Modifier,
    state: RecipesScreenState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibility: AnimatedVisibilityScope,
    onEvent: (RecipesScreenEvent) -> Unit,
    onGoToRecipe: (recipeId: String) -> Unit,
    onGoToAddRecipe: () -> Unit,
    onGoBack: () -> Unit,
) {
    val refreshState = rememberPullToRefreshState()
    val animatedExpandRotation by animateFloatAsState(
        if (state.expandFiltersTab)
            180f else 0f
    )
    with(sharedTransitionScope) {
        Scaffold(
            modifier = modifier
                .padding(bottom = 8.dp),
            topBar = {
                TwoLayerTopAppBar(modifier = Modifier
                    .clip(RoundedCornerShape(bottomStartPercent = 40, bottomEndPercent = 40)),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        actionIconContentColor = MaterialTheme.colorScheme.secondary,
                        navigationIconContentColor = MaterialTheme.colorScheme.secondary
                    ),
                    title = {
                        AnimatedContent(targetState = state.openSearch,
                            transitionSpec = {
                                if (targetState) {
                                    slideInVertically(
                                        spring(stiffness = Spring.StiffnessMediumLow),
                                        initialOffsetY = { it }) + fadeIn(
                                        spring(stiffness = Spring.StiffnessLow)
                                    ) togetherWith slideOutVertically(
                                        spring(stiffness = Spring.StiffnessMediumLow),
                                        targetOffsetY = { -it }) + fadeOut(
                                        spring(stiffness = Spring.StiffnessLow)
                                    )
                                } else {
                                    slideInVertically(
                                        spring(stiffness = Spring.StiffnessMediumLow),
                                        initialOffsetY = { -it }) + fadeIn(
                                        spring(stiffness = Spring.StiffnessLow)
                                    ) togetherWith slideOutVertically(
                                        spring(stiffness = Spring.StiffnessMediumLow),
                                        targetOffsetY = { it }) + fadeOut(
                                        spring(stiffness = Spring.StiffnessLow)
                                    )
                                }.using(SizeTransform(clip = false))
                            }) {
                            if (it)
                                TextField(modifier = Modifier
                                    .padding(vertical = 8.dp),
                                    singleLine = true,
                                    label = { Text(stringResource(R.string.recipe_name_input)) },
                                    value = state.searchString, onValueChange = {
                                        onEvent(RecipesScreenEvent.SetSearchName(it))
                                    })
                            else Text(
                                modifier = Modifier
                                    .padding(start = 8.dp),
                                text = stringResource(
                                    when (state.recipesLoadedDataType) {
                                        RecipesLoadedDataType.Favorites -> R.string.favorites_page_title
                                        RecipesLoadedDataType.OwnDataRecipes -> R.string.own_recipes_page_title
                                        else -> R.string.recipes_page_title
                                    }
                                ),
                                style = MaterialTheme.typography.headlineMedium,
                                maxLines = 2,
                                textAlign = TextAlign.Start,
                            )
                        }
                    },
                    actions = {
                        AnimatedContent(targetState = state.openSearch) {
                            if (it)
                                IconButton(
                                    onClick = {
                                        onEvent(RecipesScreenEvent.SetSearchName(""))
                                        onEvent(RecipesScreenEvent.SetOpenSearch(false))
                                    }) {
                                    Icon(Icons.Default.Clear, contentDescription = "")
                                }
                            else
                                IconButton(
                                    onClick = {
                                        onEvent(RecipesScreenEvent.SetOpenSearch(true))
                                    }) {
                                    Icon(Icons.Default.Search, contentDescription = "")
                                }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onGoBack) {
                            Icon(painter = painterResource(R.drawable.back_ic), contentDescription = "")
                        }
                    })
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onGoToAddRecipe,
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }
        ) {
            PullToRefreshBox(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                state = refreshState,
                isRefreshing = state.recipes is ApiResult.Downloading,
                onRefresh = { onEvent(RecipesScreenEvent.LoadData) },
            ) {
                when {
                    state.openFiltersPage -> {
                        when (state.loadedFilters) {
                            is ApiResult.Downloading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .wrapContentSize()
                                )
                            }

                            is ApiResult.Error -> {
                                ErrorInfoPage(
                                    errorInfo = state.loadedFilters.info
                                        ?: stringResource(R.string.unknown_error_txt),
                                    onReloadPage = { onEvent(RecipesScreenEvent.LoadFilters) }
                                )
                            }

                            is ApiResult.Succeed -> {
                                state.loadedFilters.data?.let {
                                    FiltersPage(
                                        categorizedItems = state.loadedFilters.data,
                                        onFiltersConfirmed = {
                                            onEvent(RecipesScreenEvent.SetFilters(it))
                                            onEvent(RecipesScreenEvent.SetOpenFiltersPage(false))
                                        },
                                        onCancelChanges = {
                                            onEvent(
                                                RecipesScreenEvent.SetOpenFiltersPage(false)
                                            )
                                        }
                                    )
                                } ?: Text(
                                    text = stringResource(R.string.no_saved_filters),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.W400,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .fillMaxWidth(0.7f)
                                        .clip(CircleShape)
                                        .clickable {
                                            onEvent(RecipesScreenEvent.SetExpandFiltersTab(!state.expandFiltersTab))
                                        },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .rotate(animatedExpandRotation),
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null
                                    )
                                    Text(
                                        text = stringResource(R.string.filters_lbl),
                                        style = MaterialTheme.typography.titleLarge,
                                        letterSpacing = TextUnit(
                                            0.15f,
                                            TextUnitType.Em
                                        ),
                                        textAlign = TextAlign.Start,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontWeight = FontWeight.W400,
                                    )
                                }
                                IconButton(onClick = {
                                    onEvent(RecipesScreenEvent.SetOpenFiltersPage(true))
                                }) {
                                    Icon(
                                        modifier = Modifier
                                            .size(32.dp),
                                        painter = painterResource(id = R.drawable.filter_ic),
                                        tint = MaterialTheme.colorScheme.secondary,
                                        contentDescription = "",
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .padding(4.dp)
                                ) {
                                    IconButton(onClick = {
                                        onEvent(RecipesScreenEvent.SetOpenSelectOrderingMenu(true))
                                    }) {
                                        Icon(
                                            modifier = Modifier
                                                .size(32.dp),
                                            painter = painterResource(id = R.drawable.order_ic),
                                            tint = MaterialTheme.colorScheme.secondary,
                                            contentDescription = ""
                                        )
                                    }
                                    RecipeOrderingDropDownMenu(
                                        onDismissRequest = {
                                            onEvent(
                                                RecipesScreenEvent.SetOpenSelectOrderingMenu(
                                                    false
                                                )
                                            )
                                        },
                                        onSelectOrdering = {
                                            onEvent(
                                                RecipesScreenEvent.SetOrdering(
                                                    it
                                                )
                                            )
                                        },
                                        selectedOrder = state.recipeOrdering,
                                        isAscending = state.ascending,
                                        expanded = state.openSelectOrderingMenu
                                    )
                                }
                            }
                            AnimatedVisibility(
                                state.expandFiltersTab,
                                enter = expandVertically(
                                    spring(stiffness = Spring.StiffnessMediumLow),
                                    expandFrom = Alignment.Top
                                ) + fadeIn(
                                    spring(stiffness = Spring.StiffnessLow)
                                ),
                                exit = shrinkVertically(
                                    spring(stiffness = Spring.StiffnessMediumLow),
                                    shrinkTowards = Alignment.Top
                                ) + fadeOut(
                                    spring(stiffness = Spring.StiffnessLow)
                                )
                            ) {
                                FiltersTab(modifier = Modifier
                                    .padding(start = 4.dp, end = 4.dp, bottom = 8.dp)
                                    .clip(
                                        RoundedCornerShape(
                                            bottomStartPercent = 20,
                                            bottomEndPercent = 20
                                        )
                                    )
                                    .border(
                                        BorderStroke(
                                            2.dp,
                                            MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                        shape = RoundedCornerShape(
                                            bottomStartPercent = 20,
                                            bottomEndPercent = 20
                                        )
                                    )
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                    filters = state.searchedFilters,
                                    ingredients = state.searchedIngredients,
                                    onRemoveFilter = {
                                        onEvent(
                                            RecipesScreenEvent.SetFilters(
                                                state.searchedFilters.minus(
                                                    it
                                                )
                                            )
                                        )
                                    },
                                    onAddIngredient = { onEvent(RecipesScreenEvent.SetIngredients(state.searchedIngredients.plus(it).distinct())) },
                                    onRemoveIngredient = { onEvent(RecipesScreenEvent.SetIngredients(state.searchedIngredients.minus(it))) },
                                    timeTo = state.timeTo,
                                    timeFrom = state.timeFrom,
                                    maxTime = state.maxTime,
                                    minTime = state.minTime,
                                    onConfirmFilters = { onEvent(RecipesScreenEvent.LoadData) },
                                    onClearFilters = { onEvent(RecipesScreenEvent.ClearFilters) },
                                    onTimeToChanged = { onEvent(RecipesScreenEvent.SetTimeTo(it)) },
                                    onTimeFromChanged = { onEvent(RecipesScreenEvent.SetTimeFrom(it)) }
                                )
                            }
                            LazyVerticalStaggeredGrid(
                                columns = StaggeredGridCells.Fixed(state.cellsCount.cellsCount),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalItemSpacing = 24.dp
                            ) {
                                when (state.recipes) {
                                    is ApiResult.Downloading -> items(10) {
                                        Column {
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.CenterHorizontally)
                                                    .size(200.dp)
                                                    .padding(8.dp)
                                                    .shimmerEffect()
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .height(28.dp)
                                                    .fillMaxWidth()
                                                    .padding(
                                                        start = 8.dp,
                                                        top = 12.dp,
                                                        bottom = 4.dp
                                                    )
                                                    .shimmerEffect()
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .height(64.dp)
                                                    .fillMaxWidth()
                                                    .padding(vertical = 9.dp, horizontal = 8.dp)
                                                    .shimmerEffect(),
                                            )
                                        }
                                    }

                                    is ApiResult.Error -> {
                                        item {
                                            ErrorInfoPage(
                                                errorInfo = state.recipes.info
                                                    ?: stringResource(id = R.string.unknown_error_txt),
                                                onReloadPage = { onEvent(RecipesScreenEvent.LoadData) }
                                            )
                                        }
                                    }

                                    is ApiResult.Succeed -> {
                                        if (state.recipes.data.isNullOrEmpty())
                                            item {
                                                Text(
                                                    modifier = Modifier
                                                        .alpha(0.2f)
                                                        .fillMaxSize(),
                                                    text = stringResource(R.string.no_recipes),
                                                    style = MaterialTheme.typography.headlineMedium,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        else {
                                            items(state.recipes.data) {
                                                RecipeCard(
                                                    modifier = Modifier
                                                        .padding(vertical = 4.dp, horizontal = 8.dp)
                                                        .clickable {
                                                            onGoToRecipe(it.recipeID)
                                                        },
                                                    sharedTransitionScope = sharedTransitionScope,
                                                    animatedVisibility = animatedVisibility,
                                                    recipe = it
                                                )
                                            }
                                            item(span = StaggeredGridItemSpan.FullLine) {
                                                PageSelectionRow(modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 12.dp),
                                                    totalPages = state.maxPages,
                                                    currentPage = state.currentPage,
                                                    onPageClick = {
                                                        onEvent(
                                                            RecipesScreenEvent.SetCurrentPage(
                                                                it
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
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
@Preview
private fun RecipesScreenPreview() {
    var selectedRecipe by remember {
        mutableStateOf<Recipe?>(null)
    }
    var openDialog by remember {
        mutableStateOf(false)
    }
    var state by remember {
        mutableStateOf(
            RecipesScreenState(
                maxPages = 12,
                currentPage = 1,
                recipes = ApiResult.Succeed(
                    listOf(
                        Recipe(
                            "1",
                            "",
                            "",
                            "very long name created to test its length",
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam dapibus ex sit amet purus posuere, id dapibus neque rhoncus. Donec sit amet tristique libero. Vestibulum pulvinar congue tellus, ac maximus velit maximus sit amet. Nunc cursus lectus eu sem vestibulum, ac fringilla est commodo. Cras molestie pharetra dui. Integer feugiat nec tellus eu pretium. Aenean ex nunc, suscipit ac lobortis at, laoreet vel nunc. Cras neque nunc, rutrum in scelerisque ac, fringilla ut nunc. Sed vitae est eu ipsum mollis venenatis in non sem. Praesent ullamcorper imperdiet dignissim. Donec vulputate pharetra venenatis. Mauris pellentesque consectetur arcu.\n" +
                                    "\n" +
                                    "Cras cursus ante eros, a venenatis lacus gravida ut. Sed a mi erat. Nulla ullamcorper felis sagittis felis porttitor congue. Morbi finibus neque quis sodales viverra. Vestibulum a est ac leo aliquam lacinia. Phasellus tempor eros rhoncus, facilisis libero ac, vestibulum nisi. Sed malesuada felis mi, et porttitor orci porta sed. In vitae dapibus tellus. Fusce sit amet hendrerit nibh. Interdum et malesuada fames ac ante ipsum primis in faucibus. Etiam lacinia tellus mollis metus dapibus volutpat. Vestibulum nec congue turpis, et egestas velit. Morbi et risus vel sem hendrerit sagittis in at erat.\n" +
                                    "\n" +
                                    "Nulla pharetra mauris in urna ornare, sed volutpat tellus rutrum. Suspendisse vel tincidunt tellus. Cras dapibus lectus tristique sapien tristique, eu egestas massa varius. Morbi ut elementum mauris. Suspendisse mattis faucibus nulla sed sollicitudin. Integer eget orci euismod, congue metus id, aliquam diam. Nulla hendrerit efficitur erat ac varius. Pellentesque non purus nibh. Nunc vel bibendum nisl. Nam eget ligula vitae est viverra mollis. Nullam viverra ipsum non ligula ultricies, in aliquam mi rhoncus. Nulla urna enim, tempus sed porta vel, convallis quis turpis. Aliquam vitae porttitor lacus, ut semper purus. Sed placerat lectus nunc, eget tincidunt felis mollis non. Cras enim purus, lobortis vel massa vel, egestas aliquam leo.\n" +
                                    "\n" +
                                    "Phasellus bibendum, augue eu condimentum ornare, nunc enim auctor nisl, non tristique sapien erat et augue. Maecenas varius dui ac tellus consequat, eget feugiat augue malesuada. Nullam in nibh interdum erat aliquam posuere nec sed libero. Mauris sed nibh porta, consequat turpis quis, finibus velit. Proin pharetra mollis facilisis. Morbi egestas, nisi a lacinia scelerisque, ligula lorem aliquam lorem, eu pellentesque lorem lorem et lacus. Aenean nec efficitur libero, et blandit odio.\n" +
                                    "\n" +
                                    "Vivamus eu neque pharetra, malesuada leo elementum, eleifend velit. Nulla faucibus rutrum felis a condimentum. Vivamus eu nisl eget dolor pretium elementum. Ut semper lacus odio, a varius felis luctus nec. Aliquam ac libero tincidunt arcu sollicitudin ultricies non sed velit. Cras vitae finibus purus. Maecenas facilisis, velit ut tempus accumsan, arcu risus lobortis erat, id vehicula mauris ligula id enim.",
                            emptyList(),
                            emptyList(),
                            reviewsCount = 100_000_000,
                            currentRating = 0f,
                            viewsCount = 0L
                        ),
                        Recipe(
                            "2",
                            "",
                            "",
                            "Name",
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam dapibus ex sit amet purus posuere, id dapibus neque rhoncus. Donec sit amet tristique libero. Vestibulum pulvinar congue tellus, ac maximus velit maximus sit amet. Nunc cursus lectus eu sem vestibulum, ac fringilla est commodo. Cras molestie pharetra dui. Integer feugiat nec tellus eu pretium. Aenean ex nunc, suscipit ac lobortis at, laoreet vel nunc. Cras neque nunc, rutrum in scelerisque ac, fringilla ut nunc. Sed vitae est eu ipsum mollis venenatis in non sem. Praesent ullamcorper imperdiet dignissim. Donec vulputate pharetra venenatis. Mauris pellentesque consectetur arcu.\n" +
                                    "\n" +
                                    "Cras cursus ante eros, a venenatis lacus gravida ut. Sed a mi erat. Nulla ullamcorper felis sagittis felis porttitor congue. Morbi finibus neque quis sodales viverra. Vestibulum a est ac leo aliquam lacinia. Phasellus tempor eros rhoncus, facilisis libero ac, vestibulum nisi. Sed malesuada felis mi, et porttitor orci porta sed. In vitae dapibus tellus. Fusce sit amet hendrerit nibh. Interdum et malesuada fames ac ante ipsum primis in faucibus. Etiam lacinia tellus mollis metus dapibus volutpat. Vestibulum nec congue turpis, et egestas velit. Morbi et risus vel sem hendrerit sagittis in at erat.\n" +
                                    "\n" +
                                    "Nulla pharetra mauris in urna ornare, sed volutpat tellus rutrum. Suspendisse vel tincidunt tellus. Cras dapibus lectus tristique sapien tristique, eu egestas massa varius. Morbi ut elementum mauris. Suspendisse mattis faucibus nulla sed sollicitudin. Integer eget orci euismod, congue metus id, aliquam diam. Nulla hendrerit efficitur erat ac varius. Pellentesque non purus nibh. Nunc vel bibendum nisl. Nam eget ligula vitae est viverra mollis. Nullam viverra ipsum non ligula ultricies, in aliquam mi rhoncus. Nulla urna enim, tempus sed porta vel, convallis quis turpis. Aliquam vitae porttitor lacus, ut semper purus. Sed placerat lectus nunc, eget tincidunt felis mollis non. Cras enim purus, lobortis vel massa vel, egestas aliquam leo.\n" +
                                    "\n" +
                                    "Phasellus bibendum, augue eu condimentum ornare, nunc enim auctor nisl, non tristique sapien erat et augue. Maecenas varius dui ac tellus consequat, eget feugiat augue malesuada. Nullam in nibh interdum erat aliquam posuere nec sed libero. Mauris sed nibh porta, consequat turpis quis, finibus velit. Proin pharetra mollis facilisis. Morbi egestas, nisi a lacinia scelerisque, ligula lorem aliquam lorem, eu pellentesque lorem lorem et lacus. Aenean nec efficitur libero, et blandit odio.\n" +
                                    "\n" +
                                    "Vivamus eu neque pharetra, malesuada leo elementum, eleifend velit. Nulla faucibus rutrum felis a condimentum. Vivamus eu nisl eget dolor pretium elementum. Ut semper lacus odio, a varius felis luctus nec. Aliquam ac libero tincidunt arcu sollicitudin ultricies non sed velit. Cras vitae finibus purus. Maecenas facilisis, velit ut tempus accumsan, arcu risus lobortis erat, id vehicula mauris ligula id enim.",
                            emptyList(),
                            emptyList(),
                            reviewsCount = 100_000_000,
                            currentRating = 0f,
                            viewsCount = 0L
                        ),
                        Recipe(
                            "3",
                            "",
                            "",
                            "Name",
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam dapibus ex sit amet purus posuere, id dapibus neque rhoncus. Donec sit amet tristique libero. Vestibulum pulvinar congue tellus, ac maximus velit maximus sit amet. Nunc cursus lectus eu sem vestibulum, ac fringilla est commodo. Cras molestie pharetra dui. Integer feugiat nec tellus eu pretium. Aenean ex nunc, suscipit ac lobortis at, laoreet vel nunc. Cras neque nunc, rutrum in scelerisque ac, fringilla ut nunc. Sed vitae est eu ipsum mollis venenatis in non sem. Praesent ullamcorper imperdiet dignissim. Donec vulputate pharetra venenatis. Mauris pellentesque consectetur arcu.\n" +
                                    "\n" +
                                    "Cras cursus ante eros, a venenatis lacus gravida ut. Sed a mi erat. Nulla ullamcorper felis sagittis felis porttitor congue. Morbi finibus neque quis sodales viverra. Vestibulum a est ac leo aliquam lacinia. Phasellus tempor eros rhoncus, facilisis libero ac, vestibulum nisi. Sed malesuada felis mi, et porttitor orci porta sed. In vitae dapibus tellus. Fusce sit amet hendrerit nibh. Interdum et malesuada fames ac ante ipsum primis in faucibus. Etiam lacinia tellus mollis metus dapibus volutpat. Vestibulum nec congue turpis, et egestas velit. Morbi et risus vel sem hendrerit sagittis in at erat.\n" +
                                    "\n" +
                                    "Nulla pharetra mauris in urna ornare, sed volutpat tellus rutrum. Suspendisse vel tincidunt tellus. Cras dapibus lectus tristique sapien tristique, eu egestas massa varius. Morbi ut elementum mauris. Suspendisse mattis faucibus nulla sed sollicitudin. Integer eget orci euismod, congue metus id, aliquam diam. Nulla hendrerit efficitur erat ac varius. Pellentesque non purus nibh. Nunc vel bibendum nisl. Nam eget ligula vitae est viverra mollis. Nullam viverra ipsum non ligula ultricies, in aliquam mi rhoncus. Nulla urna enim, tempus sed porta vel, convallis quis turpis. Aliquam vitae porttitor lacus, ut semper purus. Sed placerat lectus nunc, eget tincidunt felis mollis non. Cras enim purus, lobortis vel massa vel, egestas aliquam leo.\n" +
                                    "\n" +
                                    "Phasellus bibendum, augue eu condimentum ornare, nunc enim auctor nisl, non tristique sapien erat et augue. Maecenas varius dui ac tellus consequat, eget feugiat augue malesuada. Nullam in nibh interdum erat aliquam posuere nec sed libero. Mauris sed nibh porta, consequat turpis quis, finibus velit. Proin pharetra mollis facilisis. Morbi egestas, nisi a lacinia scelerisque, ligula lorem aliquam lorem, eu pellentesque lorem lorem et lacus. Aenean nec efficitur libero, et blandit odio.\n" +
                                    "\n" +
                                    "Vivamus eu neque pharetra, malesuada leo elementum, eleifend velit. Nulla faucibus rutrum felis a condimentum. Vivamus eu nisl eget dolor pretium elementum. Ut semper lacus odio, a varius felis luctus nec. Aliquam ac libero tincidunt arcu sollicitudin ultricies non sed velit. Cras vitae finibus purus. Maecenas facilisis, velit ut tempus accumsan, arcu risus lobortis erat, id vehicula mauris ligula id enim.",
                            emptyList(),
                            emptyList(),
                            reviewsCount = 100_000_000,
                            currentRating = 0f,
                            viewsCount = 0L
                        )
                    )
                ),
                cellsCount = CellsAmount.One,
                searchedFilters =
                listOf(
                    "Filetr 1",
                    "Filetr 1",
                    "Filetr 1",
                    "Filetr 1",
                    "Filetr 1",
                    "Filetr 1",
                    "Filetr 1",
                    "Filetr 1",
                ),
            )
        )
    }
    RecipeSharing_theme(darkTheme = true) {
        Surface {
            SharedTransitionLayout {
                AnimatedContent(!openDialog) {
                    if (it)
                        RecipesScreen(
                            state = state,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibility = this,
                            onEvent = {
                                if (it is RecipesScreenEvent.SetSearchName)
                                    state = state.copy(searchString = it.recipeName)
                                else if (it is RecipesScreenEvent.SetCellsAmount)
                                    state = state.copy(cellsCount = it.cellsAmount)
                                else if (it is RecipesScreenEvent.SetOpenSearch)
                                    state = state.copy(openSearch = it.openSearch)
                                else if (it is RecipesScreenEvent.SetFilters)
                                    state = state.copy(searchedFilters = it.filters)
                                else if (it is RecipesScreenEvent.SetOpenSearch)
                                    state = state.copy(openSearch = it.openSearch)
                                else if (it is RecipesScreenEvent.SetOpenSelectOrderingMenu)
                                    state = state.copy(openSelectOrderingMenu = it.openMenu)
                                else if (it is RecipesScreenEvent.SetOrdering)
                                    state = if (it.ordering == state.recipeOrdering)
                                        state.copy(ascending = !state.ascending)
                                    else state.copy(recipeOrdering = it.ordering, ascending = true)
                            },
                            onOpenMenu = {},
                            onGoToRecipe = {
                                selectedRecipe =
                                    state.recipes.data!!.firstOrNull { r -> r.recipeID == it }
                                openDialog = true
                            },
                            onGoToAddRecipe = {}
                        )
                    else RecipeConfigPage(
                        modifier = Modifier
                            .sharedBounds(
                                rememberSharedContentState(
                                    key = RecipeSharedElementKey(
                                        id = selectedRecipe!!.recipeID,
                                        origin = "recipes",
                                        type = RecipeSharedElementType.Bounds
                                    )
                                ),
                                animatedVisibilityScope = this
                            ),
                        state = RecipePageState(
                            recipe = ApiResult.Succeed(selectedRecipe),
                            recipeName = selectedRecipe?.recipeName ?: "",
                            recipeDescription = selectedRecipe?.description ?: ""
                        ),
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibility = this,
                        onEvent = {
                            if (it is RecipePageEvent.DiscardChanges)
                                openDialog = false
                        },
                        onReloadData = {},
                        onGoBack = { openDialog = false },
                        onGoToReviews = {},
                        onGoToPostReview = {}
                    )
                }
            }
        }
    }
}
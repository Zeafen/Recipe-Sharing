package com.receipts.receipt_sharing.ui.recipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.data.recipes.Ingredient
import com.receipts.receipt_sharing.data.recipes.Measure
import com.receipts.receipt_sharing.data.recipes.Recipe
import com.receipts.receipt_sharing.data.recipes.Step
import com.receipts.receipt_sharing.data.response.RecipeResult
import com.receipts.receipt_sharing.domain.viewModels.RecipePageEvent
import com.receipts.receipt_sharing.ui.ErrorInfoPage
import com.receipts.receipt_sharing.ui.shimmerEffect
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipePage(
    modifier : Modifier = Modifier,
    state : RecipePageState,
    onOpenMenu : () -> Unit,
    onEvent : (RecipePageEvent) -> Unit,
    onReloadData : () -> Unit,
    onGoToFilteredScreen : (String) -> Unit
) {
    val refreshState = rememberPullToRefreshState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer

                ),
                title = {
                    when (state.recipe) {
                        is RecipeResult.Downloading -> Box(
                            modifier = Modifier
                                .padding(vertical = 12.dp, horizontal = 8.dp)
                                .fillMaxWidth()
                                .height(32.dp)
                                .alpha(0.3f)
                                .shimmerEffect()
                        )

                        is RecipeResult.Error -> Text(
                            modifier = Modifier
                                .wrapContentHeight()
                                .padding(start = 8.dp),
                            text = stringResource(R.string.error_info_title),
                            style = MaterialTheme.typography.headlineLarge
                        )

                        is RecipeResult.Succeed -> Text(
                            modifier = Modifier
                                .wrapContentHeight()
                                .padding(start = 8.dp),
                            text = state.recipe.data?.recipeName
                                ?: stringResource(R.string.no_recipe),
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (state.recipe.data != null) {
                            onEvent(
                                if (state.isFavorite)
                                    RecipePageEvent.RemoveFromFavourites(state.recipe.data.recipeID)
                                else
                                    RecipePageEvent.AddToFavourites(state.recipe.data.recipeID)
                            )
                        }
                    }) {
                        Image(
                            painter = painterResource(
                                if (state.isFavorite) R.drawable.in_favorite_ic
                                else R.drawable.not_in_favorite_ic
                            ),
                            contentDescription = ""
                        )
                    }
                })
        },
    ) {
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            state = refreshState,
            isRefreshing = state.recipe is RecipeResult.Downloading,
            onRefresh = onReloadData,
        ) {
            when (state.recipe) {
                is RecipeResult.Downloading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .align(Alignment.CenterHorizontally)
                                .height(300.dp)
                                .width(300.dp)
                                .shimmerEffect()
                        )
                        repeat(4) {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .align(Alignment.Start)
                                    .height(24.dp)
                                    .fillMaxWidth()
                                    .shimmerEffect()
                            )
                        }
                        LazyVerticalStaggeredGrid(
                            contentPadding = PaddingValues(vertical = 36.dp),
                            columns = StaggeredGridCells.Fixed(2)
                        ) {
                            items(7) {
                                Row {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .size(12.dp)
                                            .alpha(0.3f)
                                            .clip(CircleShape)
                                            .shimmerEffect()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 8.dp, vertical = 8.dp)
                                            .align(Alignment.CenterVertically)
                                            .height(16.dp)
                                            .fillMaxWidth()
                                            .shimmerEffect()
                                    )
                                }
                            }
                        }
                        repeat(4) {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 12.dp)
                                    .align(Alignment.Start)
                                    .height(20.dp)
                                    .fillMaxWidth()
                                    .shimmerEffect()
                            )
                        }
                    }
                }

                is RecipeResult.Error -> {
                    ErrorInfoPage(
                        errorInfo = state.recipe.info
                            ?: stringResource(id = R.string.unknown_error_txt)
                    ) {
                        onReloadData()
                    }
                }

                is RecipeResult.Succeed ->
                    if (state.recipe.data != null)
                        LazyColumn(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surface),
                        ) {
                            item {
                                if (state.recipe.data.imageUrl.isNullOrEmpty())
                                    Image(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        contentScale = ContentScale.Crop,
                                        painter = painterResource(R.drawable.no_image),
                                        contentDescription = ""
                                    )
                                else
                                    AsyncImage(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(state.recipe.data.imageUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentScale = ContentScale.Crop,
                                        contentDescription = "",
                                    )
                            }
                            item {
                                LazyVerticalStaggeredGrid(
                                    modifier = Modifier
                                        .height(120.dp),
                                    columns = StaggeredGridCells.Fixed(3),
                                    contentPadding = PaddingValues(horizontal = 12.dp)
                                ) {
                                    when (state.filters) {
                                        is RecipeResult.Downloading -> {

                                        }

                                        is RecipeResult.Error -> {
                                            item {
                                                Text(
                                                    modifier = Modifier
                                                        .alpha(0.5f),
                                                    color = MaterialTheme.colorScheme.error,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    text = stringResource(R.string.filters_load_error)
                                                )
                                            }
                                        }

                                        is RecipeResult.Succeed -> {
                                            state.filters.data?.let {
                                                it.forEach {
                                                    item {
                                                        Row(
                                                            modifier = Modifier
                                                                .padding(
                                                                    vertical = 12.dp,
                                                                    horizontal = 4.dp
                                                                )
                                                                .clip(CircleShape)
                                                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                                                .clickable {
                                                                    onGoToFilteredScreen(it)
                                                                },
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.Center
                                                        ) {
                                                            Text(
                                                                modifier = Modifier
                                                                    .padding(
                                                                        vertical = 8.dp,
                                                                        horizontal = 12.dp
                                                                    ),
                                                                text = it,
                                                            )
                                                        }
                                                    }
                                                }
                                            } ?: item {
                                                Text(
                                                    modifier = Modifier
                                                        .alpha(0.6f),
                                                    style = MaterialTheme.typography.titleMedium,
                                                    text = stringResource(R.string.no_saved_filters)
                                                )
                                            }


                                        }
                                    }
                                }
                            }

                            item {
                                Text(
                                    modifier = Modifier
                                        .padding(
                                            start = 8.dp,
                                            end = 4.dp,
                                            top = 8.dp,
                                            bottom = 12.dp
                                        ),
                                    style = MaterialTheme.typography.bodyLarge,
                                    text = state.recipe.data.description
                                        ?: stringResource(R.string.no_description)
                                )
                            }
                            items(state.recipe.data.ingredients) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp, horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .size(12.dp)
                                            .alpha(0.3f)
                                            .clip(CircleShape)
                                            .background(Color.Gray)
                                    )
                                    IngredientCell(
                                        modifier = Modifier,
                                        ingredient = it
                                    )
                                }
                            }
                            item {
                                StepsRows(
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp, vertical = 8.dp),
                                    steps = state.recipe.data.steps
                                )
                            }
                        }
            }
        }
    }
}

@Composable
fun IngredientCell(
    modifier: Modifier = Modifier,
    ingredient : Ingredient
){
    Row(modifier = modifier,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(modifier = Modifier
            .alpha(0.6f)
            .padding(start = 12.dp, end = 8.dp)
            .align(Alignment.CenterVertically),
            text = ingredient.name)
        Text(modifier = Modifier
            .alpha(0.4f)
            .padding(start = 12.dp, end = 8.dp)
            .align(Alignment.CenterVertically),
            text = "${ingredient.amount}${stringResource(ingredient.measureType.shortName)}")
    }
}

@Preview
@Composable
private fun ReceiptPagePreview() {
    RecipeSharing_theme {
        RecipePage(state = RecipePageState(
            recipe = RecipeResult.Succeed(
                Recipe(
                    recipeID = "",
                    creatorID = "",
                    imageUrl = "",
                    recipeName = "Extra mayonnaise",
                    description = "Lorem ipsum dolor sit amet",
                    ingredients = listOf(
                        Ingredient("Mayonaise1", 100L, Measure.Millilitres),
                        Ingredient("Mayonaise2", 100L, Measure.Millilitres),
                        Ingredient("Mayonaise3", 100L, Measure.Millilitres),
                        Ingredient("Mayonaise4", 100L, Measure.Millilitres),
                        Ingredient("Mayonaise5", 100L, Measure.Millilitres),
                        Ingredient("Mayonaise6", 100L, Measure.Millilitres)
                    ),
                    steps = listOf(
                        Step("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse sit amet est varius, tempor tortor non, pellentesque mi. Praesent accumsan facilisis urna nec semper. Proin gravida consectetur augue. Nullam pharetra nulla at malesuada consequat. Donec eu tortor vitae risus laoreet mollis nec in ipsum. Donec sem erat, rhoncus a iaculis at, accumsan eget nisl. Nulla hendrerit dui in quam rutrum, id ultricies urna facilisis. Fusce urna augue, maximus at tortor pellentesque, laoreet auctor tortor. Maecenas ut eros enim. Donec faucibus venenatis semper. Pellentesque laoreet metus blandit arcu venenatis auctor ac non arcu.\n" +
                                "\n" +
                                "Phasellus nulla leo, condimentum in est et, ornare tincidunt neque. Morbi lectus velit, cursus quis pharetra sed, semper rhoncus felis. Pellentesque volutpat ipsum vitae mattis sodales. Proin mattis nulla velit, ac venenatis nisi euismod ut. Sed non imperdiet neque. Sed lacinia libero erat. Vestibulum id pellentesque tellus, at suscipit nulla. Duis ut erat interdum, laoreet nibh ut, lobortis est.", 123123123L),
                        Step("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse sit amet est varius, tempor tortor non, pellentesque mi. Praesent accumsan facilisis urna nec semper. Proin gravida consectetur augue. Nullam pharetra nulla at malesuada consequat. Donec eu tortor vitae risus laoreet mollis nec in ipsum. Donec sem erat, rhoncus a iaculis at, accumsan eget nisl. Nulla hendrerit dui in quam rutrum, id ultricies urna facilisis. Fusce urna augue, maximus at tortor pellentesque, laoreet auctor tortor. Maecenas ut eros enim. Donec faucibus venenatis semper. Pellentesque laoreet metus blandit arcu venenatis auctor ac non arcu.\n" +
                                "\n" +
                                "Phasellus nulla leo, condimentum in est et, ornare tincidunt neque. Morbi lectus velit, cursus quis pharetra sed, semper rhoncus felis. Pellentesque volutpat ipsum vitae mattis sodales. Proin mattis nulla velit, ac venenatis nisi euismod ut. Sed non imperdiet neque. Sed lacinia libero erat. Vestibulum id pellentesque tellus, at suscipit nulla. Duis ut erat interdum, laoreet nibh ut, lobortis est.", 123123123L),
                        Step("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse sit amet est varius, tempor tortor non, pellentesque mi. Praesent accumsan facilisis urna nec semper. Proin gravida consectetur augue. Nullam pharetra nulla at malesuada consequat. Donec eu tortor vitae risus laoreet mollis nec in ipsum. Donec sem erat, rhoncus a iaculis at, accumsan eget nisl. Nulla hendrerit dui in quam rutrum, id ultricies urna facilisis. Fusce urna augue, maximus at tortor pellentesque, laoreet auctor tortor. Maecenas ut eros enim. Donec faucibus venenatis semper. Pellentesque laoreet metus blandit arcu venenatis auctor ac non arcu.\n" +
                                "\n" +
                                "Phasellus nulla leo, condimentum in est et, ornare tincidunt neque. Morbi lectus velit, cursus quis pharetra sed, semper rhoncus felis. Pellentesque volutpat ipsum vitae mattis sodales. Proin mattis nulla velit, ac venenatis nisi euismod ut. Sed non imperdiet neque. Sed lacinia libero erat. Vestibulum id pellentesque tellus, at suscipit nulla. Duis ut erat interdum, laoreet nibh ut, lobortis est.", 123123123L),
                        Step("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse sit amet est varius, tempor tortor non, pellentesque mi. Praesent accumsan facilisis urna nec semper. Proin gravida consectetur augue. Nullam pharetra nulla at malesuada consequat. Donec eu tortor vitae risus laoreet mollis nec in ipsum. Donec sem erat, rhoncus a iaculis at, accumsan eget nisl. Nulla hendrerit dui in quam rutrum, id ultricies urna facilisis. Fusce urna augue, maximus at tortor pellentesque, laoreet auctor tortor. Maecenas ut eros enim. Donec faucibus venenatis semper. Pellentesque laoreet metus blandit arcu venenatis auctor ac non arcu.\n" +
                                "\n" +
                                "Phasellus nulla leo, condimentum in est et, ornare tincidunt neque. Morbi lectus velit, cursus quis pharetra sed, semper rhoncus felis. Pellentesque volutpat ipsum vitae mattis sodales. Proin mattis nulla velit, ac venenatis nisi euismod ut. Sed non imperdiet neque. Sed lacinia libero erat. Vestibulum id pellentesque tellus, at suscipit nulla. Duis ut erat interdum, laoreet nibh ut, lobortis est.", 123123123L),
                    )
                )
            ),
            filters = RecipeResult.Succeed(
                listOf(
                    "One",
                    "Two",
                    "Three",
                    "Four",
                    "Five",
                    "Six",
                    "One",
                    "Two",
                    "Three",
                    "Four",
                    "Five",
                    "Six",
                )
            ),
            isFavorite = true
        ),
            onOpenMenu = {},
            onEvent = {},
            onReloadData = {},
            onGoToFilteredScreen = {})
    }
}
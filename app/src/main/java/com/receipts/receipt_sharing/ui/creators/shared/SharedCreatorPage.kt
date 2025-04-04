package com.receipts.receipt_sharing.ui.creators.shared

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.data.helpers.UnsafeImageLoader
import com.receipts.receipt_sharing.data.helpers.toAmountString
import com.receipts.receipt_sharing.domain.creators.CreatorRequest
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.response.ApiResult
import com.receipts.receipt_sharing.presentation.creators.creatorPage.CreatorPageEvent
import com.receipts.receipt_sharing.presentation.creators.creatorPage.CreatorPageState
import com.receipts.receipt_sharing.ui.effects.shimmerEffect
import com.receipts.receipt_sharing.ui.infoPages.ErrorInfoPage
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme

/**
 * Composes creator info screen
 * @param state the state object user to control screen layout
 * @param modifier Modifier applied to the CreatorPage
 * @param onEvent called when user interacts with ui elements
 * @param onGoBack called when user clicks "Go back" button
 * @param onGoToRecipe called when user click on certain recipe card
 * @param onReloadData called when user update page
 * @param onGoToCreatorRecipes called when user click on "More" button in recipes list
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun CreatorPage(
    modifier: Modifier = Modifier,
    state: CreatorPageState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibility: AnimatedVisibilityScope,
    onGoBack: () -> Unit,
    onEvent: (CreatorPageEvent) -> Unit,
    onGoToCreatorRecipes: (String) -> Unit,
    onGoToRecipe: (String) -> Unit,
    onReloadData: () -> Unit
) {
    val refreshState = rememberPullToRefreshState()
    val aboutMe_rotate by animateFloatAsState(
        if (state.expandAboutMe)
            180f else 0f
    )

    with(sharedTransitionScope) {
        Scaffold(modifier = modifier,
            topBar = {
                TopAppBar(modifier = Modifier
                    .clip(RoundedCornerShape(bottomStartPercent = 40, bottomEndPercent = 40)),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        actionIconContentColor = MaterialTheme.colorScheme.secondary,
                        navigationIconContentColor = MaterialTheme.colorScheme.secondary
                    ),
                    title = {
                        Text(
                            modifier = Modifier
                                .padding(start = 8.dp),
                            text = stringResource(R.string.creator_page_header),
                            style = MaterialTheme.typography.headlineMedium,
                            maxLines = 2,
                            textAlign = TextAlign.Start,
                            overflow = TextOverflow.Ellipsis,
                            letterSpacing = TextUnit(0.1f, TextUnitType.Em),
                            fontWeight = FontWeight.W400
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onGoBack) {
                            Icon(painterResource(R.drawable.back_ic), contentDescription = "")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            onEvent(CreatorPageEvent.ChangeFollows)
                        }) {
                            Icon(
                                painter = painterResource(
                                    if (state.follows) R.drawable.in_favorite_ic
                                    else R.drawable.not_in_favorite_ic
                                ),
                                contentDescription = ""
                            )
                        }
                    })
            }
        ) {
            PullToRefreshBox(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                state = refreshState,
                isRefreshing = state.creator is ApiResult.Downloading,
                onRefresh = onReloadData,
            ) {

                when (state.creator) {
                    is ApiResult.Downloading -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 12.dp)
                                    .align(Alignment.CenterHorizontally)
                                    .height(300.dp)
                                    .width(300.dp)
                                    .shimmerEffect()
                            )
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .align(Alignment.Start)
                                    .height(24.dp)
                                    .fillMaxWidth()
                                    .shimmerEffect()
                            )
                            LazyHorizontalGrid(
                                contentPadding = PaddingValues(vertical = 36.dp, horizontal = 8.dp),
                                rows = GridCells.Fixed(2)
                            ) {
                                items(7) {
                                    Column {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.CenterHorizontally)
                                                .padding(horizontal = 8.dp)
                                                .size(128.dp)
                                                .alpha(0.3f)
                                                .shimmerEffect()
                                        )
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.Start)
                                                .padding(4.dp)
                                                .height(24.dp)
                                                .width(64.dp)
                                                .alpha(0.3f)
                                                .shimmerEffect()
                                        )
                                    }
                                }
                            }
                        }
                    }

                    is ApiResult.Error -> ErrorInfoPage(
                        modifier = Modifier
                            .padding(it),
                        errorInfo = state.creator.info
                            ?: stringResource(id = R.string.unknown_error_txt),
                        onReloadPage = onReloadData
                    )

                    is ApiResult.Succeed -> {
                        if (state.creator.data != null)
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize(),
                            ) {
                                item {
                                    if (state.creator.data.imageUrl.isEmpty())
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
                                                .clip(CircleShape)
                                                .fillMaxWidth()
                                                .padding(8.dp),
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(state.creator.data.imageUrl)
                                                .crossfade(true)
                                                .build(),
                                            imageLoader = UnsafeImageLoader.getInstance(),
                                            contentScale = ContentScale.Fit,
                                            contentDescription = "",
                                        )
                                }
                                item {
                                    Text(
                                        modifier = Modifier
                                            .padding(horizontal = 12.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.titleLarge,
                                        text = state.creator.data.nickname,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.W500,
                                        letterSpacing = TextUnit(0.2f, TextUnitType.Em)
                                    )
                                }
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                start = 8.dp,
                                                top = 24.dp,
                                                bottom = 12.dp,
                                                end = 8.dp
                                            ),
                                        verticalArrangement = Arrangement.Top,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = stringResource(R.string.creator_about_me_input),
                                                style = MaterialTheme.typography.headlineMedium,
                                                maxLines = 2,
                                                textAlign = TextAlign.Start,
                                                overflow = TextOverflow.Ellipsis,
                                                letterSpacing = TextUnit(0.1f, TextUnitType.Em),
                                                fontWeight = FontWeight.W400
                                            )
                                            IconButton(onClick = {
                                                onEvent(
                                                    CreatorPageEvent.SetExpandAboutMe(
                                                        !state.expandAboutMe
                                                    )
                                                )
                                            }) {
                                                Icon(
                                                    modifier = Modifier
                                                        .rotate(aboutMe_rotate),
                                                    imageVector = Icons.Default.KeyboardArrowDown,
                                                    contentDescription = null
                                                )
                                            }

                                        }
                                        AnimatedVisibility(
                                            visible = state.expandAboutMe,
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
                                            Text(
                                                style = MaterialTheme.typography.titleLarge,
                                                text = state.creator.data.aboutMe ?: "",
                                                textAlign = TextAlign.Center,
                                                fontWeight = FontWeight.W300,
                                                letterSpacing = TextUnit(1.5f, TextUnitType.Sp)

                                            )
                                        }
                                    }
                                }

                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceAround
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                modifier = Modifier
                                                    .padding(vertical = 4.dp, horizontal = 8.dp),
                                                text = stringResource(
                                                    id = R.string.followers_count,
                                                    state.followersCount.toAmountString()
                                                ),
                                                style = MaterialTheme.typography.titleLarge,
                                                textAlign = TextAlign.Center,
                                                fontWeight = FontWeight.W400,
                                                letterSpacing = TextUnit(2f, TextUnitType.Sp)
                                            )
                                            Button(modifier = Modifier
                                                .padding(top = 12.dp),
                                                shape = RoundedCornerShape(12.dp),
                                                onClick = { onEvent(CreatorPageEvent.ChangeFollows) }
                                            ) {
                                                Image(
                                                    if (state.follows) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                    contentDescription = "",
                                                    contentScale = ContentScale.Crop
                                                )
                                                Text(
                                                    modifier = Modifier
                                                        .padding(horizontal = 8.dp),
                                                    text = stringResource(id = R.string.follow_button_txt),
                                                    style = MaterialTheme.typography.titleLarge
                                                )
                                            }
                                        }
                                        Text(
                                            modifier = Modifier
                                                .padding(vertical = 4.dp, horizontal = 8.dp),
                                            text = stringResource(
                                                id = R.string.follows_count,
                                                state.followsCount.toAmountString()
                                            ),
                                            style = MaterialTheme.typography.titleLarge,
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.W400,
                                            letterSpacing = TextUnit(2f, TextUnitType.Sp)
                                        )
                                    }
                                }
                                item {
                                    when (state.recipes) {
                                        is ApiResult.Downloading -> {
                                            LazyRow(
                                                contentPadding = PaddingValues(
                                                    vertical = 36.dp,
                                                    horizontal = 8.dp
                                                )
                                            ) {
                                                items(7) {
                                                    Column {
                                                        Box(
                                                            modifier = Modifier
                                                                .align(Alignment.CenterHorizontally)
                                                                .padding(horizontal = 8.dp)
                                                                .size(128.dp)
                                                                .alpha(0.3f)
                                                                .shimmerEffect()
                                                        )
                                                        Box(
                                                            modifier = Modifier
                                                                .align(Alignment.Start)
                                                                .padding(4.dp)
                                                                .height(24.dp)
                                                                .width(64.dp)
                                                                .alpha(0.3f)
                                                                .shimmerEffect()
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        is ApiResult.Error -> ErrorInfoPage(
                                            modifier = Modifier
                                                .padding(vertical = 12.dp),
                                            errorInfo = state.creator.info
                                                ?: stringResource(id = R.string.unknown_error_txt)
                                        ) {
                                            onEvent(CreatorPageEvent.ReloadRecipes)
                                        }

                                        is ApiResult.Succeed -> {
                                            if (!state.recipes.data.isNullOrEmpty()) {
                                                LazyRow(
                                                    contentPadding = PaddingValues(
                                                        vertical = 36.dp,
                                                        horizontal = 8.dp
                                                    ),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    items(
                                                        state.recipes.data.take(4)
                                                    ) {
                                                        com.receipts.receipt_sharing.ui.recipe.shared.RecipeCard(
                                                            modifier = Modifier
                                                                .width(256.dp)
                                                                .padding(horizontal = 8.dp)
                                                                .clickable {
                                                                    onGoToRecipe(it.recipeID)
                                                                },
                                                            recipe = it,
                                                            sharedTransitionScope = sharedTransitionScope,
                                                            animatedVisibility = animatedVisibility
                                                        )
                                                    }
                                                    item {
                                                        Column(
                                                            modifier = Modifier
                                                                .size(256.dp)
                                                                .padding(horizontal = 8.dp)
                                                                .clip(CircleShape)
                                                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                                                .clickable {
                                                                    onGoToCreatorRecipes(state.creator.data.userID)
                                                                },
                                                            verticalArrangement = Arrangement.Center,
                                                            horizontalAlignment = Alignment.CenterHorizontally,
                                                        ) {
                                                            Icon(
                                                                modifier = Modifier
                                                                    .size(64.dp),
                                                                imageVector = Icons.Default.MoreVert,
                                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                                contentDescription = ""
                                                            )
                                                            Text(
                                                                text = stringResource(R.string.more_btn_text),
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                                style = MaterialTheme.typography.headlineLarge
                                                            )
                                                        }
                                                    }
                                                }
                                            } else {
                                                Text(
                                                    modifier = Modifier
                                                        .padding(
                                                            vertical = 36.dp,
                                                            horizontal = 8.dp
                                                        ),
                                                    text = stringResource(id = R.string.no_items),
                                                    style = MaterialTheme.typography.titleLarge,
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun CreatorInfoPreview() {
    RecipeSharing_theme(darkTheme = true) {
        Surface {
            var state by remember {
                mutableStateOf(
                    CreatorPageState(
                        creator = ApiResult.Succeed(
                            CreatorRequest(
                                "",
                                "Very Very Very Very Very Very long name",
                                "Vivamus in mauris a risus dictum consectetur. Vivamus iaculis orci id libero tempus faucibus. Suspendisse at odio eget dui imperdiet tempus. Nam sollicitudin dolor id felis congue vestibulum. Nam hendrerit justo vitae bibendum molestie. Sed nec ligula turpis. Morbi aliquet, felis tempor vulputate laoreet, ligula nulla eleifend magna, at congue nisl sapien eu erat. Mauris porta pellentesque volutpat. Curabitur condimentum dapibus massa, et facilisis odio tincidunt at. Etiam tempus suscipit iaculis.",
                                ""
                            )
                        ),
                        recipes = ApiResult.Succeed(
                            listOf(
                                Recipe(
                                    "1",
                                    "",
                                    "",
                                    "Long name",
                                    "qweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqwe",
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
                                    "Long name",
                                    "qweqweqweqweqweqwe",
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
                                    "Long name",
                                    "qweqweqweqweqweqwe",
                                    emptyList(),
                                    emptyList(),
                                    reviewsCount = 100_000_000,
                                    currentRating = 0f,
                                    viewsCount = 0L
                                ),
                                Recipe(
                                    "4",
                                    "",
                                    "",
                                    "Long name",
                                    "qweqweqweqweqweqwe",
                                    emptyList(),
                                    emptyList(),
                                    reviewsCount = 100_000_000,
                                    currentRating = 0f,
                                    viewsCount = 0L
                                ),
                                Recipe(
                                    "5",
                                    "",
                                    "",
                                    "Long name",
                                    "qweqweqweqweqweqwe",
                                    emptyList(),
                                    emptyList(),
                                    reviewsCount = 100_000_000,
                                    currentRating = 0f,
                                    viewsCount = 0L
                                ),
                                Recipe(
                                    "6",
                                    "",
                                    "",
                                    "Long name",
                                    "qweqweqweqweqweqwe",
                                    emptyList(),
                                    emptyList(),
                                    reviewsCount = 100_000_000,
                                    currentRating = 0f,
                                    viewsCount = 0L
                                ),
                            )
                        )
                    )
                )
            }
            SharedTransitionLayout {
                AnimatedVisibility(true) {
                    CreatorPage(state = state,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibility = this,
                        onEvent = {},
                        onGoBack = {},
                        onGoToCreatorRecipes = {},
                        onGoToRecipe = {},
                        onReloadData = {
                            state = state.copy(creator = ApiResult.Downloading())
                        })
                }
            }
        }
    }
}
package com.receipts.receipt_sharing.ui.creators.shared

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.ui.graphics.Color
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
import com.receipts.receipt_sharing.data.helpers.toAmountString
import com.receipts.receipt_sharing.domain.creators.CreatorRequest
import com.receipts.receipt_sharing.domain.apiServices.UnsafeImageLoader
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.response.RecipeResult
import com.receipts.receipt_sharing.presentation.creators.CreatorPageEvent
import com.receipts.receipt_sharing.presentation.creators.CreatorPageState
import com.receipts.receipt_sharing.ui.effects.shimmerEffect
import com.receipts.receipt_sharing.ui.infoPages.ErrorInfoPage
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme

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

    with(sharedTransitionScope) {
        Scaffold(modifier = modifier,
            topBar = {
                TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                    title = {
                        Text(
                            modifier = Modifier
                                .padding(start = 8.dp),
                            text = stringResource(R.string.creator_page_header),
                            style = MaterialTheme.typography.headlineLarge,
                            maxLines = 1,
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
                isRefreshing = state.creator is RecipeResult.Downloading,
                onRefresh = onReloadData,
            ) {

                when (state.creator) {
                    is RecipeResult.Downloading -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surface)
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
                                                .background(Color.Black)
                                                .shimmerEffect()
                                        )
                                    }
                                }
                            }
                        }
                    }

                    is RecipeResult.Error -> ErrorInfoPage(
                        modifier = Modifier
                            .padding(it),
                        errorInfo = state.creator.info
                            ?: stringResource(id = R.string.unknown_error_txt),
                        onReloadPage = onReloadData
                    )

                    is RecipeResult.Succeed -> {
                        if (state.creator.data != null)
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surface),
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
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 8.dp, top = 24.dp, bottom = 12.dp, end = 8.dp),
                                        style = MaterialTheme.typography.titleLarge,
                                        text = state.creator.data.aboutMe?:"",
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.W300,
                                        letterSpacing = TextUnit(1.5f, TextUnitType.Sp)

                                    )
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
                                                Text(modifier = Modifier
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
                                        is RecipeResult.Downloading -> {
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
                                                                .background(Color.Black)
                                                                .shimmerEffect()
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        is RecipeResult.Error -> ErrorInfoPage(
                                            modifier = Modifier
                                                .padding(vertical = 12.dp),
                                            errorInfo = state.creator.info
                                                ?: stringResource(id = R.string.unknown_error_txt)
                                        ) {
                                            onEvent(CreatorPageEvent.ReloadRecipes)
                                        }

                                        is RecipeResult.Succeed -> {
                                            if (!state.recipes.data.isNullOrEmpty()) {
                                                LazyRow(
                                                    contentPadding = PaddingValues(
                                                        vertical = 36.dp,
                                                        horizontal = 8.dp
                                                    )
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
                                                                .clip(RoundedCornerShape(12.dp))
                                                                .background(MaterialTheme.colorScheme.primaryContainer)
                                                                .clickable {
                                                                    onGoToCreatorRecipes(state.creator.data.imageUrl)
                                                                },
                                                            verticalArrangement = Arrangement.Center,
                                                            horizontalAlignment = Alignment.CenterHorizontally,
                                                        ) {
                                                            Icon(
                                                                modifier = Modifier
                                                                    .size(64.dp),
                                                                imageVector = Icons.Default.MoreVert,
                                                                contentDescription = ""
                                                            )
                                                            Text(
                                                                text = stringResource(R.string.more_btn_text),
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
                                                    color = MaterialTheme.colorScheme.tertiary
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
                        creator = RecipeResult.Succeed(
                            CreatorRequest(
                                "",
                                "Very Very Very Very Very Very long name",
                                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer in ultricies velit, et finibus turpis. Sed ut augue vitae lorem imperdiet vehicula. Nullam faucibus, ante vitae tristique tincidunt, ipsum elit tempor odio, non aliquam nisi risus eu erat. Donec sit amet erat nisl. Mauris egestas augue lorem, mollis interdum magna finibus et. Morbi non ullamcorper nunc. Nulla scelerisque neque eros, nec aliquam massa elementum a. Maecenas ante massa, efficitur fermentum feugiat at, semper at neque. Nullam sed nibh mattis, feugiat erat et, aliquet odio. Vestibulum posuere condimentum velit a pharetra. Maecenas lobortis tortor ut erat rutrum, eu commodo odio faucibus. In sit amet nulla imperdiet, euismod lacus eget, lobortis mi. Quisque sollicitudin porta magna at pharetra. In imperdiet ante neque, eu viverra sem aliquet pharetra.\n" +
                                        "\n" +
                                        "Aenean quis lorem nec lectus varius consequat et eu elit. Phasellus eu hendrerit nisi. Cras auctor pretium sodales. Phasellus nec vehicula urna, sed sagittis justo. Sed vitae justo vel nisi maximus tincidunt. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam feugiat rhoncus varius.\n" +
                                        "\n" +
                                        "Suspendisse potenti. Suspendisse non purus orci. Nam sit amet ex erat. Pellentesque posuere vestibulum feugiat. Duis sit amet gravida mi, eget condimentum lacus. Sed auctor, urna pharetra ultricies posuere, urna quam lobortis turpis, non auctor tellus diam id libero. Proin interdum odio nibh, vitae finibus odio varius id. Suspendisse potenti. Cras diam ex, hendrerit eget velit at, faucibus rutrum sem. Ut at velit non nibh euismod dictum id et est. Quisque rutrum congue lacinia. Aenean ultrices cursus ex sollicitudin aliquam. Aliquam erat volutpat. Aliquam varius congue sem vitae aliquet.\n" +
                                        "\n" +
                                        "Nam tellus ante, auctor rutrum vestibulum id, scelerisque nec diam. Sed odio mauris, dapibus a metus eget, vehicula gravida nibh. Suspendisse potenti. Donec in erat viverra, dapibus urna non, pharetra sem. Integer a imperdiet turpis. Curabitur ultrices sodales quam, lacinia condimentum dui fermentum id. Cras et nulla auctor, laoreet turpis eu, ullamcorper massa. Etiam nec urna eget nisl consequat viverra. Fusce et blandit mauris. Sed in ultrices mi. Phasellus eu ipsum at velit gravida tincidunt a eu arcu.\n" +
                                        "\n" +
                                        "Vivamus in mauris a risus dictum consectetur. Vivamus iaculis orci id libero tempus faucibus. Suspendisse at odio eget dui imperdiet tempus. Nam sollicitudin dolor id felis congue vestibulum. Nam hendrerit justo vitae bibendum molestie. Sed nec ligula turpis. Morbi aliquet, felis tempor vulputate laoreet, ligula nulla eleifend magna, at congue nisl sapien eu erat. Mauris porta pellentesque volutpat. Curabitur condimentum dapibus massa, et facilisis odio tincidunt at. Etiam tempus suscipit iaculis.",
                                ""
                            )
                        ),
                        recipes = RecipeResult.Succeed(
                            listOf(
                                Recipe(
                                    "1",
                                    "",
                                    "",
                                    "Long name",
                                    "qweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqwe",
                                    emptyList(),
                                    emptyList()
                                ),
                                Recipe(
                                    "2",
                                    "",
                                    "",
                                    "Long name",
                                    "qweqweqweqweqweqwe",
                                    emptyList(),
                                    emptyList()
                                ),
                                Recipe(
                                    "3",
                                    "",
                                    "",
                                    "Long name",
                                    "qweqweqweqweqweqwe",
                                    emptyList(),
                                    emptyList()
                                ),
                                Recipe(
                                    "4",
                                    "",
                                    "",
                                    "Long name",
                                    "qweqweqweqweqweqwe",
                                    emptyList(),
                                    emptyList()
                                ),
                                Recipe(
                                    "5",
                                    "",
                                    "",
                                    "Long name",
                                    "qweqweqweqweqweqwe",
                                    emptyList(),
                                    emptyList()
                                ),
                                Recipe(
                                    "6",
                                    "",
                                    "",
                                    "Long name",
                                    "qweqweqweqweqweqwe",
                                    emptyList(),
                                    emptyList()
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
                            state = state.copy(creator = RecipeResult.Downloading())
                        })
                }
            }
        }
    }
}

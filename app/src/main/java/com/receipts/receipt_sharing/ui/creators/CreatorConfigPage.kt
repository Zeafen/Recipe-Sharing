package com.receipts.receipt_sharing.ui.creators

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.receipts.receipt_sharing.domain.CreatorRequest
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.response.RecipeResult
import com.receipts.receipt_sharing.domain.apiServices.UnsafeImageLoader
import com.receipts.receipt_sharing.data.viewModels.CreatorPageEvent
import com.receipts.receipt_sharing.ui.ErrorInfoPage
import com.receipts.receipt_sharing.ui.recipe.RecipeCard
import com.receipts.receipt_sharing.ui.shimmerEffect
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorConfigPage(
    modifier: Modifier = Modifier,
    state : CreatorPageState,
    onEvent : (CreatorPageEvent) -> Unit,
    onOpenMenu : () -> Unit,
    onGoToAddRecipePage : () -> Unit,
    onGoToFollowers : () -> Unit) {

    val photoLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
            onEvent(CreatorPageEvent.SetImageUrl(uri))
        }
    val refreshState = rememberPullToRefreshState()

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
                        text = stringResource(id = R.string.creator_page_header),
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "")
                    }
                },
                actions = {
                    IconButton(onClick = { onEvent(CreatorPageEvent.SaveChanges) }) {
                        Icon(Icons.Default.Check, contentDescription = "")
                    }
                })
        }) {
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            state = refreshState,
            isRefreshing = state.creator is RecipeResult.Downloading,
            onRefresh = { onEvent(CreatorPageEvent.LoadUserInfo) },
        ) {
            when (state.creator) {
                is RecipeResult.Downloading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface),
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
                    errorInfo = state.creator.info?: stringResource(
                        id = R.string.unknown_error_txt),
                    onReloadPage = { onEvent(CreatorPageEvent.LoadUserInfo) }
                )
                is RecipeResult.Succeed -> {
                    if (state.creator.data != null)
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            item {
                                if (state.imageUrl.isNullOrEmpty())
                                    Image(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                            .clickable {
                                                photoLauncher.launch(
                                                    PickVisualMediaRequest
                                                        .Builder()
                                                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                        .build()
                                                )
                                            },
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
                                            .data(state.creator.data.imageUrl)
                                            .crossfade(true)
                                            .build(),
                                        imageLoader = UnsafeImageLoader.getInstance(),
                                        contentScale = ContentScale.Fit,
                                        contentDescription = "",
                                    )
                            }

                            item {
                                Column {

                                    OutlinedTextField(
                                        modifier = Modifier
                                            .padding(
                                                start = 12.dp,
                                                end = 4.dp,
                                                top = 8.dp,
                                                bottom = 12.dp
                                            ),
                                        value = state.creatorName,
                                        label = { Text(text = stringResource(id = R.string.creator_name_input)) },
                                        onValueChange = {
                                            onEvent(CreatorPageEvent.SetCreatorName(it))
                                        }
                                    )
                                }
                            }

                            item {
                                Row(
                                    modifier = Modifier
                                        .padding(start = 24.dp, end = 4.dp, bottom = 24.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable {
                                            onGoToFollowers()
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(
                                        colors = ButtonDefaults.textButtonColors(
                                            contentColor = MaterialTheme.colorScheme.primary,
                                            disabledContentColor = MaterialTheme.colorScheme.onSurface,
                                        ),
                                        onClick = { /*TODO*/ }) {
                                        Text(
                                            modifier = Modifier
                                                .padding(vertical = 4.dp, horizontal = 8.dp),
                                            text = stringResource(
                                                id = R.string.followers_count,
                                                state.followers
                                            ),
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                    }
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
                                        errorInfo = state.creator.info
                                            ?: stringResource(id = R.string.unknown_error_txt)
                                    ) {
                                        onEvent(CreatorPageEvent.LoadUserInfo)
                                    }

                                    is RecipeResult.Succeed -> {
                                        LazyRow(
                                            contentPadding = PaddingValues(
                                                vertical = 36.dp,
                                                horizontal = 8.dp
                                            ),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (!state.recipes.data.isNullOrEmpty())
                                                items(state.recipes.data.take(4)) {
                                                    RecipeCard(
                                                        modifier = Modifier
                                                            .width(256.dp)
                                                            .padding(horizontal = 8.dp),
                                                        recipe = it
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
                                                            onGoToAddRecipePage()
                                                        },
                                                    verticalArrangement = Arrangement.Center,
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                ) {
                                                    Icon(
                                                        modifier = Modifier
                                                            .size(64.dp),
                                                        imageVector = Icons.Default.Add,
                                                        contentDescription = ""
                                                    )
                                                    Text(
                                                        text = stringResource(R.string.add_txt),
                                                        style = MaterialTheme.typography.headlineLarge
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

@Preview
@Composable
private fun Preview() {
    RecipeSharing_theme {
        Surface {
            CreatorConfigPage(state = CreatorPageState(
                creator = RecipeResult.Succeed(
                    CreatorRequest(
                        userID = "",
                        nickname = "",
                        imageUrl = ""
                    ),
                ),
                creatorName = "Some name to test",
                recipes = RecipeResult.Succeed(
                    listOf(

                        Recipe(
                            "",
                            "",
                            "",
                            "Long name",
                            "qweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqwe",
                            emptyList(),
                            emptyList()
                        ),
                        Recipe(
                            "",
                            "",
                            "",
                            "Long name",
                            "qweqweqweqweqweqwe",
                            emptyList(),
                            emptyList()
                        ),
                        Recipe(
                            "",
                            "",
                            "",
                            "Long name",
                            "qweqweqweqweqweqwe",
                            emptyList(),
                            emptyList()
                        ),
                        Recipe(
                            "",
                            "",
                            "",
                            "Long name",
                            "qweqweqweqweqweqwe",
                            emptyList(),
                            emptyList()
                        ),
                        Recipe(
                            "",
                            "",
                            "",
                            "Long name",
                            "qweqweqweqweqweqwe",
                            emptyList(),
                            emptyList()
                        ),
                        Recipe(
                            "",
                            "",
                            "",
                            "Long name",
                            "qweqweqweqweqweqwe",
                            emptyList(),
                            emptyList()
                        ),
                    )
                )
            ),
                onEvent = {},
                onOpenMenu = {},
                onGoToAddRecipePage = {},
                onGoToFollowers = {})
        }
    }
}
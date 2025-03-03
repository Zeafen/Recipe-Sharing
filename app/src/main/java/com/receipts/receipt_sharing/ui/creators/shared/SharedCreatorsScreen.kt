package com.receipts.receipt_sharing.ui.creators

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.domain.CreatorRequest
import com.receipts.receipt_sharing.domain.response.RecipeResult
import com.receipts.receipt_sharing.presentation.creators.CreatorsScreenEvent
import com.receipts.receipt_sharing.presentation.creators.CreatorsScreenState
import com.receipts.receipt_sharing.ui.ErrorInfoPage
import com.receipts.receipt_sharing.ui.shimmerEffect
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorsScreen(
    modifier: Modifier = Modifier,
    state : CreatorsScreenState,
    onOpenMenu: () -> Unit,
    onGoToCreatorPage : (creatorId : String) -> Unit,
    onEvent : (CreatorsScreenEvent) -> Unit
    ) {
    val refreshState = rememberPullToRefreshState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(modifier = Modifier,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                title = {
                    TextField(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                        label = { Text(stringResource(R.string.creator_name_input)) },
                        value = state.searchedName,
                        onValueChange = { onEvent(CreatorsScreenEvent.SetSearchName(it)) }
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            onEvent(CreatorsScreenEvent.SetSearchName(""))
                        }) {
                        Icon(Icons.Default.Clear, contentDescription = "")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "")
                    }
                })
        },
    ) {
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            state = refreshState,
            isRefreshing = state.creators is RecipeResult.Downloading,
            onRefresh = {
                onEvent(
                    if (state.followsLoaded)
                        CreatorsScreenEvent.LoadData
                    else CreatorsScreenEvent.LoadFollows
                )
            },
        ) {
            when (state.creators) {
                is RecipeResult.Downloading -> {
                    LazyColumn(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .fillMaxSize(),
                    ) {
                        items(10) {
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 8.dp, horizontal = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(vertical = 4.dp, horizontal = 8.dp)
                                        .align(Alignment.CenterVertically)
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .alpha(0.5f)
                                        .shimmerEffect()
                                )
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp)
                                        .align(Alignment.CenterVertically)
                                        .fillMaxWidth()
                                        .height(20.dp)
                                        .shimmerEffect()
                                )
                            }
                        }
                    }
                }

                is RecipeResult.Error -> {
                    ErrorInfoPage(errorInfo = state.creators.info
                        ?: stringResource(id = R.string.unknown_error_txt),
                        onReloadPage = {
                            onEvent(
                                if (state.followsLoaded)
                                    CreatorsScreenEvent.LoadData
                                else CreatorsScreenEvent.LoadFollows
                            )
                        })
                }

                is RecipeResult.Succeed -> {
                    LazyColumn(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .fillMaxSize(),
                    ) {
                        if (state.creators.data != null)
                            items(state.creators.data) {
                                CreatorCell(
                                    modifier = Modifier
                                        .padding(vertical = 12.dp, horizontal = 4.dp)
                                        .clickable {
                                            onGoToCreatorPage(it.userID)
                                        },
                                    creator = it
                                )
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
    RecipeSharing_theme(darkTheme = true) {
        Surface {
            CreatorsScreen(state = CreatorsScreenState(
                creators = RecipeResult.Succeed(
                    listOf(
                        CreatorRequest("", "Very Very Very Very Very Very Very Very Very Very Very long name", ""),
                        CreatorRequest("", "Very Very Very Very Very Very Very Very Very Very Very long name", ""),
                        CreatorRequest("", "Very Very Very Very Very Very Very Very Very Very Very long name", ""),
                        CreatorRequest("", "Very Very Very Very Very Very Very Very Very Very Very long name", ""),
                        CreatorRequest("", "Very Very Very Very Very Very Very Very Very Very Very long name", ""),
                        CreatorRequest("", "Very Very Very Very Very Very Very Very Very Very Very long name", ""),
                        CreatorRequest("", "Very Very Very Very Very Very Very Very Very Very Very long name", "")
                    )
                )
            ),
                onOpenMenu = {},
                onGoToCreatorPage = {},
                onEvent = {})
        }
    }
}
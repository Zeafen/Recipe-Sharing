package com.receipts.receipt_sharing.ui.creators.shared

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.domain.creators.CreatorRequest
import com.receipts.receipt_sharing.domain.response.ApiResult
import com.receipts.receipt_sharing.presentation.creators.creatorsScreen.CreatorLoadDataType
import com.receipts.receipt_sharing.presentation.creators.creatorsScreen.CreatorsScreenEvent
import com.receipts.receipt_sharing.presentation.creators.creatorsScreen.CreatorsScreenState
import com.receipts.receipt_sharing.ui.PageSelectionRow
import com.receipts.receipt_sharing.ui.TwoLayerTopAppBar
import com.receipts.receipt_sharing.ui.effects.shimmerEffect
import com.receipts.receipt_sharing.ui.infoPages.ErrorInfoPage
import com.receipts.receipt_sharing.ui.recipe.elements.ColumnAmountDropDownMenu
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme

/**
 * Composes
 * @param state the state object user to control screen layout
 * @param modifier Modifier applied to the CreatorsScreen
 * @param onEvent called when user interacts with ui elements
 * @param onOpenMenu called when user click on menu button
 * @param onGoToCreatorPage called when user click on creator card
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun CreatorsScreen(
    modifier: Modifier = Modifier,
    state: CreatorsScreenState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibility: AnimatedVisibilityScope,
    onOpenMenu: () -> Unit,
    onGoToCreatorPage: (creatorId: String) -> Unit,
    onEvent: (CreatorsScreenEvent) -> Unit
) {
    val refreshState = rememberPullToRefreshState()
    Scaffold(

        modifier = modifier,
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
                    AnimatedContent(targetState = state.openSearchString,
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
                        }) { targetState ->
                        if (targetState)
                            TextField(modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                                singleLine = true,
                                label = { Text(stringResource(R.string.creator_name_input)) },
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            onEvent(CreatorsScreenEvent.SetSearchName(""))
                                            onEvent(CreatorsScreenEvent.SetOpenSearchString(false))
                                        }) {
                                        Icon(Icons.Default.Clear, contentDescription = "")
                                    }
                                },
                                value = state.searchedName,
                                onValueChange = { onEvent(CreatorsScreenEvent.SetSearchName(it)) }
                            )
                        else Text(
                            modifier = Modifier
                                .padding(start = 8.dp),
                            text = stringResource(when(state.loadDataType){
                                CreatorLoadDataType.All -> R.string.creator_screen_header
                                is CreatorLoadDataType.Followers -> R.string.followers_screen_header
                                is CreatorLoadDataType.Follows -> R.string.follows_page_title
                            }),
                            style = MaterialTheme.typography.headlineMedium,
                            maxLines = 2,
                            textAlign = TextAlign.Start,
                        )
                    }
                },
                actions = {
                    AnimatedVisibility(
                        !state.openSearchString,
                        enter = slideInVertically(
                            spring(stiffness = Spring.StiffnessMediumLow),
                            initialOffsetY = { -it }) + fadeIn(
                            spring(stiffness = Spring.StiffnessLow)
                        ),
                        exit = slideOutVertically(
                            spring(stiffness = Spring.StiffnessMediumLow),
                            targetOffsetY = { it }) + fadeOut(
                            spring(stiffness = Spring.StiffnessLow)
                        )
                    ) {
                        IconButton(
                            onClick = {
                                onEvent(CreatorsScreenEvent.SetOpenSearchString(true))
                            }) {
                            Icon(Icons.Default.Search, contentDescription = "")
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "")
                    }
                })
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        onEvent(
                            CreatorsScreenEvent.SetOpenSelectCellsAmountDialog(
                                true
                            )
                        )
                    },
                    shape = CircleShape
                ) {
                    Icon(
                        painterResource(R.drawable.cells_amount_ic),
                        contentDescription = null
                    )
                }
                ColumnAmountDropDownMenu(
                    onDismissRequest = {
                        onEvent(CreatorsScreenEvent.SetOpenSelectCellsAmountDialog(false))
                    },
                    onSelectSize = {
                        onEvent(CreatorsScreenEvent.SetCellsAmount(it))
                    },
                    expanded = state.openCellsAmountSelect
                )
            }
        }
    ) {
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            state = refreshState,
            isRefreshing = state.creators is ApiResult.Downloading,
            onRefresh = { onEvent(CreatorsScreenEvent.LoadData) },
        ) {
            when (state.creators) {
                is ApiResult.Downloading -> {
                    LazyColumn(
                        modifier = Modifier
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

                is ApiResult.Error -> {
                    ErrorInfoPage(errorInfo = state.creators.info
                        ?: stringResource(id = R.string.unknown_error_txt),
                        onReloadPage = {
                            onEvent(
                                CreatorsScreenEvent.LoadData
                            )
                        })
                }

                is ApiResult.Succeed -> {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(state.cellsAmount.cellsCount),
                        modifier = Modifier
                            .background(Color.Transparent)
                            .fillMaxSize(),
                    ) {
                        if (state.creators.data != null) {
                            items(state.creators.data) {
                                CreatorCell(
                                    modifier = Modifier
                                        .padding(vertical = 12.dp, horizontal = 4.dp)
                                        .clickable {
                                            onGoToCreatorPage(it.userID)
                                        },
                                    creator = it,
                                    sharedTransitionScope = sharedTransitionScope,
                                    animatedVisibility = animatedVisibility
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
                                            CreatorsScreenEvent.SetCurrentPage(
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun Preview() {
    RecipeSharing_theme(darkTheme = true) {
        var state by remember {
            mutableStateOf(
                CreatorsScreenState(
                    creators = ApiResult.Succeed(
                        listOf(
                            CreatorRequest(
                                "1",
                                "Very Very Very Very Very Very Very Very Very Very Very long name",
                                "",
                                ""
                            ),
                            CreatorRequest(
                                "2",
                                "Very Very Very Very Very Very Very Very Very Very Very long name",
                                "",
                                ""
                            ),
                            CreatorRequest(
                                "3",
                                "Very Very Very Very Very Very Very Very Very Very Very long name",
                                "",
                                ""
                            ),
                            CreatorRequest(
                                "4",
                                "Very Very Very Very Very Very Very Very Very Very Very long name",
                                "",
                                ""
                            ),
                            CreatorRequest(
                                "5",
                                "Very Very Very Very Very Very Very Very Very Very Very long name",
                                "",
                                ""
                            ),
                            CreatorRequest(
                                "6",
                                "Very Very Very Very Very Very Very Very Very Very Very long name",
                                "",
                                ""
                            ),
                            CreatorRequest(
                                "7",
                                "Very Very Very Very Very Very Very Very Very Very Very long name",
                                "",
                                ""
                            )
                        )
                    ),
                    currentPage = 5,
                    maxPages = 12,
                )
            )
        }
        Surface {
            SharedTransitionLayout {
                AnimatedVisibility(true) {
                    CreatorsScreen(
                        state = state,
                        onOpenMenu = {},
                        onGoToCreatorPage = {},
                        onEvent = {
                            if (it is CreatorsScreenEvent.SetOpenSelectCellsAmountDialog)
                                state = state.copy(openCellsAmountSelect = it.openDialog)
                            else if (it is CreatorsScreenEvent.SetCellsAmount)
                                state = state.copy(cellsAmount = it.cellsAmount)
                        },
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibility = this,
                    )
                }
            }
        }
    }
}
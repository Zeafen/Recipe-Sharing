package com.receipts.receipt_sharing.ui.reviews

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.receipts.receipt_sharing.domain.response.ApiResult
import com.receipts.receipt_sharing.domain.reviews.ReviewModel
import com.receipts.receipt_sharing.presentation.reviews.reviewsScreen.ReviewsScreenEvent
import com.receipts.receipt_sharing.presentation.reviews.reviewsScreen.ReviewsScreenState
import com.receipts.receipt_sharing.ui.PageSelectionRow
import com.receipts.receipt_sharing.ui.effects.shimmerEffect
import com.receipts.receipt_sharing.ui.infoPages.ErrorInfoPage
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme
import java.util.Locale

/**
 * Composes reviews screen
 * @param modifier Modifier applied to ReviewsScreen
 * @param state state object user to control layout
 * @param onEditClick called when user clicks on "Edit" button on own review card
 * @param onEvent called when user interacts with ui
 * @param onGoBack called when user clicks on "Back" navigation button
 * @param onReloadPage called when user tries to reload screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsScreen(
    modifier: Modifier = Modifier,
    state: ReviewsScreenState,
    onEvent: (ReviewsScreenEvent) -> Unit,
    onGoBack: () -> Unit,
    onReloadPage: () -> Unit,
    onEditClick: (String) -> Unit
) {
    val refreshState = rememberPullToRefreshState()
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
                navigationIcon = {
                    IconButton(onClick = onGoBack) {
                        Icon(
                            painter = painterResource(R.drawable.back_ic),
                            contentDescription = null
                        )
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        if (state.recipeImageUrl.isEmpty())
                            Image(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.FillWidth,
                                painter = painterResource(R.drawable.no_image),
                                contentDescription = null,
                            )
                        else
                            AsyncImage(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Fit,
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(state.recipeImageUrl)
                                    .crossfade(true)
                                    .build(),
                                imageLoader = UnsafeImageLoader.getInstance(),
                                contentDescription = ""
                            )
                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp),
                            text = state.recipeName.ifEmpty { stringResource(R.string.no_recipe) },
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            letterSpacing = TextUnit(0.15f, TextUnitType.Em),
                            fontWeight = FontWeight.W500,
                        )
                        Icon(
                            modifier = Modifier
                                .padding(start = 12.dp, end = 4.dp)
                                .alpha(0.5f),
                            painter = painterResource(R.drawable.star_ic),
                            contentDescription = null
                        )
                        Text(
                            modifier = Modifier
                                .alpha(0.5f),
                            text = state.reviews.data?.let {
                                String.format(
                                    Locale.getDefault(),
                                    "%.1f",
                                    (it.sumOf { review -> review.rating }.toFloat() / it.size)
                                )
                            } ?: "0f",
                            style = MaterialTheme.typography.bodyLarge,
                            letterSpacing = TextUnit(0.15f, TextUnitType.Em),
                            fontWeight = FontWeight.W400,
                        )

                    }
                }
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            modifier = Modifier
                .padding(innerPadding),
            state = refreshState,
            isRefreshing = state.reviews is ApiResult.Downloading,
            onRefresh = onReloadPage
        ) {
            if (state.openConfirmDeleteDialog && state.ownReview.data != null)
                AlertDialog(
                    onDismissRequest = { onEvent(ReviewsScreenEvent.CloseDialogs) },
                    confirmButton = {
                        TextButton(
                            modifier = Modifier
                                .padding(horizontal = 8.dp),
                            onClick = {
                                onEvent(ReviewsScreenEvent.DeleteReview(state.ownReview.data.id))
                            },
                            shape = CircleShape
                        ) {
                            Text(
                                text = stringResource(R.string.confirm_txt),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleLarge,
                                letterSpacing = TextUnit(
                                    1.5f,
                                    TextUnitType.Sp
                                ),
                                fontWeight = FontWeight.W500
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(
                            modifier = Modifier
                                .padding(horizontal = 8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            onClick = {
                                onEvent(ReviewsScreenEvent.CloseDialogs)
                            },
                            shape = CircleShape
                        ) {
                            Text(
                                text = stringResource(R.string.cancel_txt),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleLarge,
                                letterSpacing = TextUnit(
                                    1.5f,
                                    TextUnitType.Sp
                                ),
                                fontWeight = FontWeight.W500
                            )
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null
                        )
                    },
                    title = {
                        Text(
                            text = stringResource(R.string.confirm_delete_title),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineMedium,
                            overflow = TextOverflow.Ellipsis,
                            letterSpacing = TextUnit(
                                0.1f,
                                TextUnitType.Em
                            ),
                            fontWeight = FontWeight.W500
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(
                                R.string.confirm_delete_review_text,
                                state.ownReview.data.text
                            ),
                            textAlign = TextAlign.Justify,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            letterSpacing = TextUnit(
                                2f,
                                TextUnitType.Sp
                            ),
                            fontWeight = FontWeight.W400
                        )
                    },
                )

            LazyColumn {
                when (state.ownReview) {
                    is ApiResult.Downloading -> item {
                        Column(
                            modifier = Modifier
                                .padding(vertical = 8.dp, horizontal = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.Top,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(128.dp)
                                        .padding(8.dp)
                                        .shimmerEffect()
                                )
                                Box(
                                    modifier = Modifier
                                        .height(48.dp)
                                        .width(136.dp)
                                        .padding(8.dp)
                                        .shimmerEffect()
                                )
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .padding(8.dp)
                                        .shimmerEffect()
                                )
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .padding(8.dp)
                                        .shimmerEffect()
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .height(128.dp)
                                    .fillMaxWidth()
                                    .padding(vertical = 9.dp, horizontal = 8.dp)
                                    .shimmerEffect(),
                            )
                        }
                    }

                    is ApiResult.Error -> {}
                    is ApiResult.Succeed ->
                        state.ownReview.data?.let {
                            item {
                                Text(
                                    modifier = Modifier
                                        .padding(vertical = 12.dp, horizontal = 8.dp),
                                    text = stringResource(R.string.own_reviews_lbl),
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
                            item {
                                ReviewCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    review = it,
                                    onEditClick = { onEditClick(state.ownReview.data.id) },
                                    onDeleteClick = { onEvent(ReviewsScreenEvent.OpenConfirmDeleteDialog) }
                                )
                            }
                        }
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp, end = 8.dp, start = 8.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier
                                .weight(1f),
                            text = stringResource(R.string.recipe_tab_reviews),
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
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                        ) {
                            IconButton(onClick = {
                                onEvent(ReviewsScreenEvent.SetOpenSortingBox(true))
                            }) {
                                Icon(
                                    modifier = Modifier
                                        .size(32.dp),
                                    painter = painterResource(id = R.drawable.filter_ic),
                                    tint = MaterialTheme.colorScheme.secondary,
                                    contentDescription = ""
                                )
                            }
                            androidx.compose.animation.AnimatedVisibility(state.openSortingBox) {
                                ReviewSortingDropDownMenu(
                                    expanded = state.openSortingBox,
                                    onDismissRequest = { onEvent(ReviewsScreenEvent.CloseDialogs) },
                                    onSelectSorting = { onEvent(ReviewsScreenEvent.SetSorting(it)) })
                            }
                        }
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                        ) {
                            IconButton(onClick = {
                                onEvent(ReviewsScreenEvent.SetOpenOrderingBox(true))
                            }) {
                                Icon(
                                    modifier = Modifier
                                        .size(32.dp),
                                    painter = painterResource(id = R.drawable.order_ic),
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                            androidx.compose.animation.AnimatedVisibility(state.openOrderingBox) {
                                ReviewOrderDropDownMenu(
                                    expanded = state.openOrderingBox,
                                    selectedOrder = state.selectedOrdering,
                                    isAscending = state.isAscending,
                                    onDismissRequest = { onEvent(ReviewsScreenEvent.CloseDialogs) },
                                    onSelectOrdering = {
                                        onEvent(
                                            ReviewsScreenEvent.SetOrdering(
                                                it
                                            )
                                        )
                                    })
                            }
                        }
                    }
                }
                when (state.reviews) {
                    is ApiResult.Downloading -> items(5) {
                        Column(
                            modifier = Modifier
                                .padding(vertical = 8.dp, horizontal = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.Top,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(128.dp)
                                        .padding(8.dp)
                                        .shimmerEffect()
                                )
                                Box(
                                    modifier = Modifier
                                        .height(48.dp)
                                        .width(256.dp)
                                        .padding(8.dp)
                                        .shimmerEffect()
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .height(128.dp)
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 8.dp)
                                    .shimmerEffect(),
                            )
                        }
                    }

                    is ApiResult.Error -> item {
                        ErrorInfoPage(
                            errorInfo = state.reviews.info
                                ?: stringResource(id = R.string.unknown_error_txt),
                            onReloadPage = { onReloadPage() }
                        )
                    }

                    is ApiResult.Succeed -> {
                        state.reviews.data?.let { reviews ->
                            items(reviews) {
                                ReviewCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp, horizontal = 12.dp),
                                    review = it
                                )
                            }
                        }
                        item {
                            PageSelectionRow(modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                                totalPages = state.totalPages,
                                currentPage = state.currentPage,
                                onPageClick = {
                                    onEvent(
                                        ReviewsScreenEvent.SetCurrentPage(
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


@Preview
@Composable
private fun Preview() {
    var state by remember {
        mutableStateOf(
            ReviewsScreenState(
                recipeName = "kask;lsadjaksdjasdjlsajdlsakjdlksajdlsadjlksadjlksadjlsakdj",
                ownReview = ApiResult.Succeed(
                    ReviewModel(
                        "", "User name", "", "some text", 3
                    )
                ),
                reviews = ApiResult.Succeed(
                    listOf(
                        ReviewModel(
                            "", "User name", "", "some text", 3
                        ),
                        ReviewModel(
                            "", "User name", "", "some text", 3
                        ),
                        ReviewModel(
                            "", "User name", "", "some text", 3
                        ),
                        ReviewModel(
                            "", "User name", "", "some text", 3
                        ),
                        ReviewModel(
                            "", "User name", "", "some text", 3
                        ),
                        ReviewModel(
                            "", "User name", "", "some text", 3
                        ),
                    )
                ),
            )
        )
    }
    RecipeSharing_theme {
        Surface {
            ReviewsScreen(state = state,
                onGoBack = {},
                onEvent = {
                    if (it is ReviewsScreenEvent.SetOpenSortingBox)
                        state = state.copy(
                            openSortingBox = it.openDialog
                        )
                    if (it is ReviewsScreenEvent.SetOpenOrderingBox)
                        state = state.copy(
                            openOrderingBox = it.openDialog
                        )
                    if (it is ReviewsScreenEvent.CloseDialogs)
                        state = state.copy(
                            openOrderingBox = false,
                            openSortingBox = false,
                            openConfirmDeleteDialog = false
                        )
                },
                onReloadPage = {},
                onEditClick = {})
        }
    }
}
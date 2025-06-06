package com.receipts.receipt_sharing.ui.reviews

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.receipts.receipt_sharing.presentation.reviews.reviewPage.ReviewPageEvent
import com.receipts.receipt_sharing.presentation.reviews.reviewPage.ReviewPageState
import com.receipts.receipt_sharing.presentation.reviews.reviewPage.ReviewPageViewModel
import com.receipts.receipt_sharing.ui.effects.shimmerEffect
import com.receipts.receipt_sharing.ui.infoPages.ErrorInfoPage
import com.receipts.receipt_sharing.ui.recipe.elements.RatingRow
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme

/**
 * Composes review editing page
 * @param modifier Modifier applied to ReviewPage
 * @param state state object used to control layout
 * @param onEvent called when user interacts with ui
 * @param onRefresh called when user updates page
 * @param onGoBack called when user clicks on "Back" navigation button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewPage(
    modifier: Modifier = Modifier,
    state: ReviewPageState,
    onEvent: (ReviewPageEvent) -> Unit,
) {
    val ctx = LocalContext.current
    LaunchedEffect(state.infoMessage) {
        if (!state.infoMessage.isNullOrEmpty())
            Toast.makeText(ctx, state.infoMessage, Toast.LENGTH_SHORT).show()
    }
    Scaffold(
        modifier = modifier,
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

                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(ReviewPageEvent.GoBack) }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onEvent(ReviewPageEvent.ConfirmChanges) },
                        enabled = !state.isError
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.send_ic),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            modifier = Modifier
                .padding(innerPadding),
            isRefreshing = state.review is ApiResult.Downloading,
            onRefresh = { onEvent(ReviewPageEvent.Refresh) },
        ) {
            LazyColumn {
                when (state.review) {
                    is ApiResult.Downloading -> {
                        item {
                            Row(
                                verticalAlignment = Alignment.Top,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .padding(8.dp)
                                        .clip(CircleShape)
                                        .shimmerEffect()
                                )
                                Box(
                                    modifier = Modifier
                                        .height(48.dp)
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .shimmerEffect()
                                )
                            }
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .height(256.dp)
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .shimmerEffect()
                            )
                        }
                    }

                    is ApiResult.Error -> {
                        item {
                            ErrorInfoPage(
                                errorInfo = state.review.info
                                    ?: stringResource(R.string.unknown_error_txt),
                            ) { onEvent(ReviewPageEvent.Refresh) }
                        }
                    }

                    is ApiResult.Succeed -> {
                        item {
                            Row(
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .padding(top = 12.dp, bottom = 8.dp, start = 8.dp, end = 4.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.Top
                            ) {
                                if (state.userImageUrl.isBlank())
                                    Image(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .widthIn(64.dp, 128.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.FillWidth,
                                        painter = painterResource(R.drawable.no_image),
                                        contentDescription = null,
                                    )
                                else
                                    AsyncImage(
                                        modifier = Modifier
                                            .widthIn(64.dp, 128.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Fit,
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(state.userImageUrl)
                                            .crossfade(true)
                                            .build(),
                                        imageLoader = UnsafeImageLoader.getInstance(),
                                        contentDescription = ""
                                    )
                                Text(
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp, vertical = 12.dp),
                                    text = state.userName,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.titleLarge,
                                    letterSpacing = TextUnit(
                                        1.5f,
                                        TextUnitType.Sp
                                    ),
                                    fontWeight = FontWeight.W500
                                )
                            }
                        }
                        item {
                            RatingRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentSize(),
                                starSize = 40.dp,
                                itemsPadding = PaddingValues(horizontal = 8.dp),
                                onStarClick = { onEvent(ReviewPageEvent.SetReviewRating(it)) },
                                currentRating = state.reviewRating,
                            )
                        }
                        item {
                            OutlinedTextField(modifier = Modifier
                                .defaultMinSize(minHeight = 128.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                value = state.reviewText,

                                placeholder = {
                                    Text(
                                        modifier = Modifier
                                            .alpha(0.5f),
                                        text = stringResource(R.string.review_text_input_tip),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.W400,
                                        letterSpacing = TextUnit(1.5f, TextUnitType.Sp)
                                    )
                                },
                                isError = state.isError,
                                supportingText = {
                                    AnimatedVisibility(state.isError,
                                        enter = slideInVertically(spring(stiffness = Spring.StiffnessMediumLow)) { -it } + fadeIn(
                                            spring(stiffness = Spring.StiffnessLow)
                                        ),
                                        exit = slideOutVertically(spring(stiffness = Spring.StiffnessMediumLow)) { it } + fadeOut(
                                            spring(stiffness = Spring.StiffnessLow)
                                        )) {
                                        Text(
                                            text =
                                            if (state.reviewText.isEmpty())
                                                stringResource(R.string.empty_field_error)
                                            else if (state.reviewText.length < ReviewPageViewModel.TEXT_MIN_LENGTH)
                                                stringResource(
                                                    R.string.incorrect_length_least_error,
                                                    ReviewPageViewModel.TEXT_MIN_LENGTH
                                                )
                                            else if (state.reviewText.split(" ").size < ReviewPageViewModel.WORDS_MIN_COUNT)
                                                stringResource(
                                                    R.string.incorrect_words_least_error,
                                                    ReviewPageViewModel.WORDS_MIN_COUNT
                                                )
                                            else stringResource(R.string.rating_not_selected_error),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.W400,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                },
                                onValueChange = { onEvent(ReviewPageEvent.SetReviewText(it)) }
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
    val state by remember {
        mutableStateOf(
            ReviewPageState(
                review = ApiResult.Succeed(
                    ReviewModel(
                        "",
                        "", "", "", 4
                    ),
                ),
                userName = "Some name",
                recipeName = "Some name"
            )
        )
    }
    RecipeSharing_theme {
        Surface {
            ReviewPage(
                state = state,
                onEvent = {},
            )
        }
    }
}
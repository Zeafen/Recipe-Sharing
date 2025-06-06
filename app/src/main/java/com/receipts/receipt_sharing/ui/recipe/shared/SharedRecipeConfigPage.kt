package com.receipts.receipt_sharing.ui.recipe.shared

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.receipts.receipt_sharing.data.helpers.UnsafeImageLoader
import com.receipts.receipt_sharing.data.helpers.toAmountString
import com.receipts.receipt_sharing.domain.recipes.Ingredient
import com.receipts.receipt_sharing.domain.recipes.Measure
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.recipes.RecipeDifficulty
import com.receipts.receipt_sharing.domain.recipes.Step
import com.receipts.receipt_sharing.domain.response.ApiResult
import com.receipts.receipt_sharing.domain.reviews.ReviewModel
import com.receipts.receipt_sharing.presentation.RecipeSharedElementKey
import com.receipts.receipt_sharing.presentation.RecipeSharedElementType
import com.receipts.receipt_sharing.presentation.recipes.recipePage.RecipePageEvent
import com.receipts.receipt_sharing.presentation.recipes.recipePage.RecipePageState
import com.receipts.receipt_sharing.presentation.recipes.recipePage.RecipeTab
import com.receipts.receipt_sharing.ui.dialogs.IngredientConfigureDialog
import com.receipts.receipt_sharing.ui.dialogs.StepConfigureDialog
import com.receipts.receipt_sharing.ui.effects.shimmerEffect
import com.receipts.receipt_sharing.ui.filters.FiltersPage
import com.receipts.receipt_sharing.ui.infoPages.ErrorInfoPage
import com.receipts.receipt_sharing.ui.recipe.elements.IngredientCell
import com.receipts.receipt_sharing.ui.recipe.elements.RatingRow
import com.receipts.receipt_sharing.ui.recipe.steps.EditableStepsRows
import com.receipts.receipt_sharing.ui.reviews.ReviewCard
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme

/**
 * composes Recipe information|configuration page
 * @param modifier Modifeir applied to RecoRecipeConfigPage
 * @param state state object used to control page layout
 * @param onEvent called when user interacts with ui
 * @param onGoBack called when user clicks "Back" navigation button
 * @param onReloadData called when user updates page
 * @param onGoToPostReview called when user clicks "Review" action button
 * @param onGoToReviews called when user clicks "All reviews" button in reviews section
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun RecipeConfigPage(
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibility: AnimatedVisibilityScope,
    state: RecipePageState,
    onEvent: (RecipePageEvent) -> Unit,
    onGoBack: () -> Unit,
    onReloadData: () -> Unit,
    onGoToReviews: (String) -> Unit,
    onGoToPostReview: () -> Unit,
) {
    val fraction by animateFloatAsState(
        if (state.isEditingRecord && state.own)
            0.4f
        else 0.8f
    )
    val localContext = LocalContext.current
    val photoLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            onEvent(RecipePageEvent.SetImageUrl(uri))
        }
    val refreshState = rememberPullToRefreshState()
    val pagerState = rememberPagerState {
        RecipeTab.entries.size
    }
    LaunchedEffect(state.recipeName.isEmpty() || state.ingredients.isEmpty() || state.steps.isEmpty()) {
        onEvent(RecipePageEvent.SetIsError(state.recipeName.isEmpty() || state.ingredients.isEmpty() || state.steps.isEmpty()))
    }

    LaunchedEffect(state.infoMessage) {
        if (!state.infoMessage.isNullOrEmpty())
            Toast.makeText(localContext, state.infoMessage, Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(state.selectedRecipeTabIndex) {
        pagerState.animateScrollToPage(state.selectedRecipeTabIndex)
    }
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress)
            onEvent(RecipePageEvent.SetSelectedRecipeTabIndex(pagerState.currentPage))
    }

    with(sharedTransitionScope) {
        Scaffold(modifier = modifier,
            topBar = {
                TopAppBar(modifier = Modifier
                    .clip(RoundedCornerShape(bottomStartPercent = 40, bottomEndPercent = 40)),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        navigationIconContentColor = MaterialTheme.colorScheme.secondary,
                        actionIconContentColor = MaterialTheme.colorScheme.secondary,
                    ),
                    navigationIcon = {
                        IconButton(onClick = onGoBack) {
                            Icon(
                                painter = painterResource(R.drawable.back_ic),
                                contentDescription = ""
                            )
                        }
                    },
                    title = {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .padding(start = 8.dp),
                            text = stringResource(R.string.recipe_page_title),
                            style = MaterialTheme.typography.headlineMedium,
                            maxLines = 2,
                            textAlign = TextAlign.Start,
                            overflow = TextOverflow.Ellipsis,
                            letterSpacing = TextUnit(0.1f, TextUnitType.Em),
                            fontWeight = FontWeight.W400
                        )
                    },
                    actions = {
                        AnimatedContent(targetState = state.isEditingRecord && state.own) { inEditMode ->
                            when {
                                inEditMode ->
                                    IconButton(
                                        onClick = {
                                            onEvent(RecipePageEvent.DiscardChanges)
                                        },
                                        enabled = !state.recipe.data?.recipeID.isNullOrEmpty()
                                    ) {
                                        Icon(Icons.Default.Clear, contentDescription = "'")
                                    }

                                else -> {
                                    if (state.own)
                                        Row {
                                            IconButton(onClick = {
                                                onEvent(RecipePageEvent.EditRecord)
                                            }) {
                                                Icon(
                                                    painter = painterResource(R.drawable.edit_ic),
                                                    contentDescription = ""
                                                )
                                            }
                                            IconButton(
                                                onClick = {
                                                    onEvent(RecipePageEvent.OpenConfirmDeleteDialog)
                                                },
                                                enabled = !state.recipe.data?.recipeID.isNullOrEmpty()
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = ""
                                                )
                                            }
                                        }
                                    else Row {
                                        IconButton(
                                            onClick = onGoToPostReview,
                                            enabled = !state.recipe.data?.recipeID.isNullOrEmpty()
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.comment_ic),
                                                contentDescription = ""
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                onEvent(RecipePageEvent.ChangeIsFavorite)
                                            },
                                            enabled = !state.recipe.data?.recipeID.isNullOrEmpty()
                                        ) {
                                            Icon(
                                                painter = painterResource(
                                                    if (state.isFavorite) R.drawable.in_favorite_ic
                                                    else R.drawable.not_in_favorite_ic
                                                ),
                                                contentDescription = ""
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    })
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = state.own && state.isEditingRecord && !state.openFiltersPage,
                    enter = slideInVertically(
                        spring(stiffness = Spring.StiffnessMediumLow),
                        initialOffsetY = { offset -> -offset }) + fadeIn(spring(stiffness = Spring.StiffnessMedium)),
                    exit = slideOutVertically(
                        spring(stiffness = Spring.StiffnessMediumLow),
                        targetOffsetY = { offset -> offset }) + fadeOut(spring(stiffness = Spring.StiffnessMedium))
                ) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 12.dp),
                        onClick = {
                            onEvent(RecipePageEvent.SaveChanges)
                        },
                        enabled = !state.isError
                    ) {
                        Text(
                            text = stringResource(R.string.confirm_txt),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }) {
            PullToRefreshBox(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                state = refreshState,
                isRefreshing = state.recipe is ApiResult.Downloading,
                onRefresh = onReloadData,
            ) {
                if (state.openFiltersPage)
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
                                onReloadPage = { onEvent(RecipePageEvent.LoadAllFilters) }
                            )
                        }

                        is ApiResult.Succeed -> {
                            state.loadedFilters.data?.let {
                                FiltersPage(
                                    categorizedItems = state.loadedFilters.data,
                                    onFiltersConfirmed = {
                                        onEvent(RecipePageEvent.SetFilters(it))
                                        onEvent(RecipePageEvent.SetOpenFiltersPage(false))
                                    },
                                    onCancelChanges = {
                                        onEvent(
                                            RecipePageEvent.SetOpenFiltersPage(false)
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
                else
                    when (state.recipe) {
                        is ApiResult.Downloading -> {
                            Column {
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

                        is ApiResult.Error -> {
                            ErrorInfoPage(
                                errorInfo = state.recipe.info
                                    ?: stringResource(id = R.string.unknown_error_txt),
                                onReloadPage = onReloadData
                            )
                        }

                        is ApiResult.Succeed -> {
                            if (state.recipe.data != null) {
                                if (state.openAddIngredientDialog)
                                    IngredientConfigureDialog(
                                        ingredient = state.selectedIngredient,
                                        onDismissRequest = {
                                            onEvent(RecipePageEvent.CloseDialogs)
                                        },
                                        onSaveChanges = { ingr ->
                                            if (state.selectedIngrIndex in state.ingredients.indices)
                                                onEvent(
                                                    RecipePageEvent.UpdateIngredient(
                                                        state.selectedIngrIndex,
                                                        ingr
                                                    )
                                                )
                                            else onEvent(RecipePageEvent.AddIngredient(ingr))
                                            onEvent(RecipePageEvent.CloseDialogs)
                                        })
                                if (state.openAddStepDialog)
                                    StepConfigureDialog(
                                        step = state.selectedStep,
                                        onDismissRequest = { onEvent(RecipePageEvent.CloseDialogs) },
                                        onSaveChanges = { step ->
                                            onEvent(
                                                if (state.selectedStepIndex in state.steps.indices) RecipePageEvent.UpdateStep(
                                                    state.selectedStepIndex,
                                                    step
                                                )
                                                else RecipePageEvent.AddStep(step)
                                            )
                                            onEvent(RecipePageEvent.CloseDialogs)
                                        })

                                if (state.openConfirmDeleteDialog)
                                    AlertDialog(
                                        onDismissRequest = { onEvent(RecipePageEvent.CloseDialogs) },
                                        confirmButton = {
                                            TextButton(
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp),
                                                onClick = {
                                                    onEvent(RecipePageEvent.DeleteRecipe)
                                                    onGoBack()
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
                                                    onEvent(RecipePageEvent.CloseDialogs)
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
                                                    R.string.confirm_delete_recipe_text,
                                                    state.recipeName
                                                ),
                                                textAlign = TextAlign.Justify,
                                                style = MaterialTheme.typography.bodyLarge,
                                                overflow = TextOverflow.Ellipsis,
                                                letterSpacing = TextUnit(
                                                    2f,
                                                    TextUnitType.Sp
                                                ),
                                                fontWeight = FontWeight.W400
                                            )
                                        },
                                    )
                                Column(
                                    modifier = Modifier
                                        .background(Color.Transparent)
                                ) {
                                    if (state.imageUrl.isNullOrEmpty())
                                        Image(
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .sharedElement(
                                                    rememberSharedContentState(
                                                        key = RecipeSharedElementKey(
                                                            id = state.recipe.data.recipeID,
                                                            origin = "recipes",
                                                            type = RecipeSharedElementType.Image
                                                        )
                                                    ),
                                                    animatedVisibilityScope = animatedVisibility
                                                )
                                                .fillMaxWidth()
                                                .wrapContentWidth()
                                                .fillMaxWidth(fraction)
                                                .clickable {
                                                    if (state.own && state.isEditingRecord)
                                                        photoLauncher.launch(
                                                            PickVisualMediaRequest
                                                                .Builder()
                                                                .setMediaType(
                                                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                                                )
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
                                                .padding(8.dp)
                                                .sharedElement(
                                                    rememberSharedContentState(
                                                        key = RecipeSharedElementKey(
                                                            id = state.recipe.data.recipeID,
                                                            origin = "recipes",
                                                            type = RecipeSharedElementType.Image
                                                        )
                                                    ),
                                                    animatedVisibilityScope = animatedVisibility
                                                )
                                                .fillMaxWidth()
                                                .wrapContentWidth()
                                                .fillMaxWidth(fraction)
                                                .clickable {
                                                    if (state.own && state.isEditingRecord)
                                                        photoLauncher.launch(
                                                            PickVisualMediaRequest
                                                                .Builder()
                                                                .setMediaType(
                                                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                                                )
                                                                .build()
                                                        )
                                                },
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(state.imageUrl)
                                                .crossfade(true)
                                                .build(),
                                            imageLoader = UnsafeImageLoader.getInstance(),
                                            contentScale = ContentScale.Fit,
                                            contentDescription = "",
                                        )

                                    ScrollableTabRow(modifier = Modifier.fillMaxWidth(),
                                        selectedTabIndex = state.selectedRecipeTabIndex,
                                        tabs = {
                                            RecipeTab.entries.forEachIndexed { index, recipeTab ->
                                                Tab(selected = state.selectedRecipeTabIndex == index,
                                                    onClick = {
                                                        onEvent(
                                                            RecipePageEvent.SetSelectedRecipeTabIndex(
                                                                index
                                                            )
                                                        )
                                                    },
                                                    text = {
                                                        Text(
                                                            text = stringResource(recipeTab.tabName),
                                                            style = MaterialTheme.typography.titleLarge,
                                                            letterSpacing = TextUnit(
                                                                0.15f,
                                                                TextUnitType.Em
                                                            ),
                                                            textAlign = TextAlign.Justify,
                                                            fontWeight = FontWeight.W400,
                                                        )
                                                    })
                                            }
                                        }
                                    )
                                    HorizontalPager(
                                        modifier = Modifier
                                            .weight(1f),
                                        state = pagerState
                                    ) { page ->
                                        when (RecipeTab.entries[page]) {
                                            RecipeTab.Info -> {
                                                LazyColumn(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                ) {
                                                    item {
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(bottom = 12.dp),
                                                            horizontalArrangement = Arrangement.End,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            LazyRow(
                                                                modifier = Modifier
                                                                    .weight(1f)
                                                            ) {
                                                                if (!state.filters.data.isNullOrEmpty())
                                                                    items(state.filters.data) {
                                                                        Row(
                                                                            modifier = Modifier
                                                                                .padding(
                                                                                    vertical = 12.dp,
                                                                                    horizontal = 4.dp
                                                                                )
                                                                                .clip(CircleShape)
                                                                                .background(
                                                                                    MaterialTheme.colorScheme.tertiaryContainer
                                                                                ),
                                                                            verticalAlignment = Alignment.CenterVertically,
                                                                            horizontalArrangement = Arrangement.SpaceBetween
                                                                        ) {
                                                                            Text(
                                                                                modifier = Modifier
                                                                                    .padding(8.dp),
                                                                                text = it,
                                                                                style = MaterialTheme.typography.bodyLarge,
                                                                                fontWeight = FontWeight.W400,
                                                                                letterSpacing = TextUnit(
                                                                                    0.1f,
                                                                                    TextUnitType.Em
                                                                                ),
                                                                                maxLines = 1,
                                                                            )
                                                                            AnimatedVisibility(state.own && state.isEditingRecord) {
                                                                                IconButton(onClick = {
                                                                                    onEvent(
                                                                                        RecipePageEvent.SetFilters(
                                                                                            state.filters.data.minus(
                                                                                                it
                                                                                            )
                                                                                        )
                                                                                    )
                                                                                }) {
                                                                                    Icon(
                                                                                        Icons.Default.Clear,
                                                                                        contentDescription = ""
                                                                                    )
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                else {
                                                                    item {
                                                                        Text(
                                                                            modifier = Modifier
                                                                                .padding(
                                                                                    vertical = 12.dp,
                                                                                    horizontal = 8.dp
                                                                                )
                                                                                .alpha(0.35f),
                                                                            text = stringResource(id = R.string.no_saved_filters),
                                                                            style = MaterialTheme.typography.headlineSmall,
                                                                            textAlign = TextAlign.Center
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                            AnimatedVisibility(
                                                                state.own && state.isEditingRecord,
                                                                enter = slideInHorizontally(
                                                                    spring(stiffness = Spring.StiffnessMediumLow),
                                                                    initialOffsetX = { offset -> offset * 2 }) + fadeIn(
                                                                    spring(stiffness = Spring.StiffnessMedium)
                                                                ),
                                                                exit = slideOutHorizontally(
                                                                    spring(stiffness = Spring.StiffnessMediumLow),
                                                                    targetOffsetX = { offset -> offset * 2 }) + fadeOut(
                                                                    spring(stiffness = Spring.StiffnessMedium)
                                                                )
                                                            ) {
                                                                IconButton(onClick = {
                                                                    onEvent(
                                                                        RecipePageEvent.SetOpenFiltersPage(
                                                                            true
                                                                        )
                                                                    )
                                                                }) {
                                                                    Icon(
                                                                        modifier = Modifier
                                                                            .size(32.dp),
                                                                        painter = painterResource(id = R.drawable.filter_ic),
                                                                        tint = MaterialTheme.colorScheme.secondary,
                                                                        contentDescription = ""
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                    item {
                                                        AnimatedContent(
                                                            modifier = Modifier
                                                                .padding(
                                                                    start = 8.dp,
                                                                    end = 4.dp,
                                                                    top = 8.dp,
                                                                    bottom = 12.dp
                                                                )
                                                                .sharedElement(
                                                                    rememberSharedContentState(
                                                                        key = RecipeSharedElementKey(
                                                                            id = state.recipe.data.recipeID,
                                                                            origin = "recipes",
                                                                            type = RecipeSharedElementType.Title
                                                                        )
                                                                    ),
                                                                    animatedVisibilityScope = animatedVisibility
                                                                )
                                                                .skipToLookaheadSize()
                                                                .fillMaxWidth(),
                                                            targetState = state.own && state.isEditingRecord,
                                                            transitionSpec = {
                                                                if (targetState) {
                                                                    slideInHorizontally(
                                                                        spring(stiffness = Spring.StiffnessMediumLow),
                                                                        initialOffsetX = { offset -> offset }) + fadeIn(
                                                                        spring(stiffness = Spring.StiffnessMedium)
                                                                    ) togetherWith
                                                                            slideOutHorizontally(
                                                                                spring(stiffness = Spring.StiffnessMediumLow),
                                                                                targetOffsetX = { offset -> -offset }) + fadeOut(
                                                                        spring(stiffness = Spring.StiffnessMedium)
                                                                    )
                                                                } else {
                                                                    slideInHorizontally(
                                                                        spring(stiffness = Spring.StiffnessMediumLow),
                                                                        initialOffsetX = { offset -> -offset }) + fadeIn(
                                                                        spring(stiffness = Spring.StiffnessMedium)
                                                                    ) togetherWith
                                                                            slideOutHorizontally(
                                                                                spring(stiffness = Spring.StiffnessMediumLow),
                                                                                targetOffsetX = { offset -> offset }) + fadeOut(
                                                                        spring(stiffness = Spring.StiffnessMedium)
                                                                    )
                                                                }.using(SizeTransform(clip = false))
                                                            }
                                                        ) { inEditMode ->
                                                            if (inEditMode)
                                                                OutlinedTextField(
                                                                    value = state.recipeName,
                                                                    label = {
                                                                        Text(
                                                                            text = stringResource(
                                                                                R.string.recipe_name_label
                                                                            )
                                                                        )
                                                                    },
                                                                    textStyle = MaterialTheme.typography.bodyLarge,
                                                                    singleLine = true,
                                                                    onValueChange = {
                                                                        onEvent(
                                                                            RecipePageEvent.SetRecipeName(
                                                                                it
                                                                            )
                                                                        )
                                                                    },
                                                                    isError = state.recipeName.isEmpty(),
                                                                    supportingText = {
                                                                        if (state.recipeName.isEmpty())
                                                                            Text(
                                                                                text = stringResource(
                                                                                    R.string.empty_field_error
                                                                                ),
                                                                                style = MaterialTheme.typography.bodyMedium,
                                                                                fontWeight = FontWeight.W400,
                                                                                color = MaterialTheme.colorScheme.error
                                                                            )
                                                                    })
                                                            else {
                                                                val difficultyTint = remember(state.recipe.data.difficulty) {
                                                                    when (state.recipe.data.difficulty) {
                                                                        RecipeDifficulty.Beginner -> Color(
                                                                            0xFF388E3C
                                                                        )

                                                                        RecipeDifficulty.Common -> Color(
                                                                            0xFFCCC916
                                                                        )

                                                                        RecipeDifficulty.Adept -> Color(
                                                                            0xFFE3700B
                                                                        )

                                                                        RecipeDifficulty.MasterPiece -> Color(
                                                                            0xFFFF0000
                                                                        )
                                                                    }
                                                                }
                                                                Row(
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                    horizontalArrangement = Arrangement.Absolute.SpaceAround
                                                                ) {
                                                                    Text(
                                                                        text = state.recipeName,
                                                                        textAlign = TextAlign.Justify,
                                                                        style = MaterialTheme.typography.headlineMedium,
                                                                        overflow = TextOverflow.Ellipsis,
                                                                        letterSpacing = TextUnit(
                                                                            0.1f,
                                                                            TextUnitType.Em
                                                                        ),
                                                                        fontWeight = FontWeight.W500
                                                                    )
                                                                    Row(verticalAlignment = Alignment.CenterVertically){
                                                                        Icon(
                                                                            painter = painterResource(
                                                                                R.drawable.kitchen_ic
                                                                            ),
                                                                            contentDescription = null,
                                                                            tint = difficultyTint
                                                                        )
                                                                        Text(
                                                                            text = stringResource(
                                                                                state.recipe.data.difficulty.nameRes
                                                                            ),
                                                                            textAlign = TextAlign.Justify,
                                                                            style = MaterialTheme.typography.titleLarge,
                                                                            overflow = TextOverflow.Ellipsis,
                                                                            letterSpacing = TextUnit(
                                                                                0.1f,
                                                                                TextUnitType.Em
                                                                            ),
                                                                            color = difficultyTint,
                                                                            fontWeight = FontWeight.W400
                                                                        )
                                                                    }

                                                                }
                                                            }
                                                        }
                                                    }
                                                    item {
                                                        AnimatedVisibility(
                                                            visible = !state.isEditingRecord && state.own || !state.own,
                                                            enter = expandVertically(
                                                                spring(stiffness = Spring.StiffnessMediumLow),
                                                                expandFrom = Alignment.Top
                                                            ) + fadeIn(
                                                                spring(stiffness = Spring.StiffnessMedium)
                                                            ),
                                                            exit = shrinkVertically(
                                                                spring(stiffness = Spring.StiffnessMediumLow),
                                                                shrinkTowards = Alignment.Top
                                                            ) + fadeOut(
                                                                spring(stiffness = Spring.StiffnessMedium)
                                                            )
                                                        ) {
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.Start
                                                            ) {
                                                                RatingRow(currentRating = state.recipeRating)
                                                                Text(
                                                                    text = "(${state.recipeReviewsCount.toAmountString()})",
                                                                    textAlign = TextAlign.Start,
                                                                    style = MaterialTheme.typography.bodyMedium,
                                                                    overflow = TextOverflow.Ellipsis,
                                                                    letterSpacing = TextUnit(
                                                                        2f,
                                                                        TextUnitType.Sp
                                                                    ),
                                                                    fontWeight = FontWeight.W400
                                                                )
                                                            }
                                                        }
                                                    }
                                                    item {
                                                        AnimatedContent(
                                                            modifier = Modifier
                                                                .padding(
                                                                    start = 8.dp,
                                                                    end = 4.dp,
                                                                    top = 8.dp,
                                                                    bottom = 12.dp
                                                                )
                                                                .sharedElement(
                                                                    rememberSharedContentState(
                                                                        key = RecipeSharedElementKey(
                                                                            id = state.recipe.data.recipeID,
                                                                            origin = "recipes",
                                                                            type = RecipeSharedElementType.Description
                                                                        )
                                                                    ),
                                                                    animatedVisibilityScope = animatedVisibility
                                                                )
                                                                .skipToLookaheadSize()
                                                                .fillMaxWidth(),
                                                            targetState = state.own && state.isEditingRecord,
                                                            transitionSpec = {
                                                                if (targetState) {
                                                                    slideInHorizontally(
                                                                        spring(stiffness = Spring.StiffnessMediumLow),
                                                                        initialOffsetX = { offset -> offset }) + fadeIn(
                                                                        spring(stiffness = Spring.StiffnessMedium)
                                                                    ) togetherWith
                                                                            slideOutHorizontally(
                                                                                spring(stiffness = Spring.StiffnessMediumLow),
                                                                                targetOffsetX = { offset -> -offset }) + fadeOut(
                                                                        spring(stiffness = Spring.StiffnessMedium)
                                                                    )
                                                                } else {
                                                                    slideInHorizontally(
                                                                        spring(stiffness = Spring.StiffnessMediumLow),
                                                                        initialOffsetX = { offset -> -offset }) + fadeIn(
                                                                        spring(stiffness = Spring.StiffnessMedium)
                                                                    ) togetherWith
                                                                            slideOutHorizontally(
                                                                                spring(stiffness = Spring.StiffnessMediumLow),
                                                                                targetOffsetX = { offset -> offset }) + fadeOut(
                                                                        spring(stiffness = Spring.StiffnessMedium)
                                                                    )
                                                                }.using(SizeTransform(clip = false))
                                                            }
                                                        ) { inEditMode ->
                                                            if (inEditMode)
                                                                OutlinedTextField(
                                                                    value = state.recipeDescription,
                                                                    label = {
                                                                        Text(
                                                                            text = stringResource(
                                                                                R.string.recipe_description_label
                                                                            )
                                                                        )
                                                                    },
                                                                    textStyle = MaterialTheme.typography.bodyLarge,
                                                                    onValueChange = {
                                                                        onEvent(
                                                                            RecipePageEvent.SetRecipeDescription(
                                                                                it
                                                                            )
                                                                        )
                                                                    })
                                                            else Text(
                                                                text = state.recipeDescription,
                                                                textAlign = TextAlign.Justify,
                                                                style = MaterialTheme.typography.bodyLarge,
                                                                overflow = TextOverflow.Ellipsis,
                                                                letterSpacing = TextUnit(
                                                                    2f,
                                                                    TextUnitType.Sp
                                                                ),
                                                                fontWeight = FontWeight.W400
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            RecipeTab.Ingredients -> {
                                                LazyColumn(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(8.dp)
                                                ) {
                                                    if (state.ingredients.isEmpty())
                                                        item {
                                                            Text(
                                                                text = stringResource(R.string.no_ingredients),
                                                                style = MaterialTheme.typography.bodyMedium,
                                                                fontWeight = FontWeight.W400,
                                                                color = MaterialTheme.colorScheme.error
                                                            )
                                                        }
                                                    item {
                                                        AnimatedVisibility(
                                                            state.own && state.isEditingRecord,
                                                            enter = slideInVertically(
                                                                spring(stiffness = Spring.StiffnessMediumLow),
                                                                initialOffsetY = { offset -> -offset }) + fadeIn(
                                                                spring(stiffness = Spring.StiffnessMedium)
                                                            ),
                                                            exit = slideOutVertically(
                                                                spring(stiffness = Spring.StiffnessMediumLow),
                                                                targetOffsetY = { offset -> -offset }) + fadeOut(
                                                                spring(stiffness = Spring.StiffnessMedium)
                                                            )
                                                        ) {
                                                            OutlinedButton(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .padding(
                                                                        vertical = 4.dp,
                                                                        horizontal = 12.dp
                                                                    ),
                                                                onClick = {
                                                                    onEvent(RecipePageEvent.SetSelectedIngredient())
                                                                    onEvent(
                                                                        RecipePageEvent.SetOpenIngredientConfigDialog(
                                                                            true
                                                                        )
                                                                    )
                                                                }
                                                            ) {
                                                                Icon(
                                                                    Icons.Default.Add,
                                                                    contentDescription = ""
                                                                )
                                                                Text(
                                                                    style = MaterialTheme.typography.titleMedium,
                                                                    text = stringResource(id = R.string.add_ingredient_str)
                                                                )
                                                            }
                                                        }
                                                    }
                                                    items(state.ingredients) {
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(
                                                                    vertical = 12.dp,
                                                                    horizontal = 4.dp
                                                                ),
                                                            horizontalArrangement = Arrangement.Center,
                                                            verticalAlignment = Alignment.CenterVertically
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
                                                                ingredient = it
                                                            )
                                                            AnimatedVisibility(state.own && state.isEditingRecord) {
                                                                Row {
                                                                    IconButton(onClick = {
                                                                        onEvent(
                                                                            RecipePageEvent.SetSelectedIngredient(
                                                                                it
                                                                            )
                                                                        )
                                                                        onEvent(
                                                                            RecipePageEvent.SetOpenIngredientConfigDialog(
                                                                                true
                                                                            )
                                                                        )
                                                                    }) {
                                                                        Icon(
                                                                            Icons.Default.Edit,
                                                                            contentDescription = "",
                                                                            tint = MaterialTheme.colorScheme.secondary
                                                                        )
                                                                    }
                                                                    IconButton(onClick = {
                                                                        onEvent(
                                                                            RecipePageEvent.RemoveIngredient(
                                                                                it
                                                                            )
                                                                        )
                                                                    }) {
                                                                        Icon(
                                                                            Icons.Default.Delete,
                                                                            contentDescription = "",
                                                                            tint = MaterialTheme.colorScheme.secondary
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            RecipeTab.Instructions -> {
                                                LazyColumn(modifier = Modifier.fillMaxSize()) {

                                                    item {
                                                        EditableStepsRows(modifier = Modifier
                                                            .padding(
                                                                vertical = 4.dp,
                                                                horizontal = 8.dp
                                                            ),
                                                            steps = state.steps,
                                                            canEdit = state.own && state.isEditingRecord,
                                                            onAddClick = {
                                                                onEvent(RecipePageEvent.SetSelectedStep())
                                                                onEvent(
                                                                    RecipePageEvent.SetOpenStepConfigDialog(
                                                                        true
                                                                    )
                                                                )
                                                            },
                                                            onEditClick = {
                                                                onEvent(
                                                                    RecipePageEvent.SetSelectedStep(
                                                                        it
                                                                    )
                                                                )
                                                                onEvent(
                                                                    RecipePageEvent.SetOpenStepConfigDialog(
                                                                        true
                                                                    )
                                                                )
                                                            },
                                                            onDeleteClick = {
                                                                onEvent(
                                                                    RecipePageEvent.RemoveStep(
                                                                        it
                                                                    )
                                                                )
                                                            })
                                                    }
                                                }
                                            }

                                            RecipeTab.Reviews -> {
                                                LazyColumn {
                                                    when (state.reviews) {
                                                        is ApiResult.Downloading -> item {
                                                            CircularProgressIndicator(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .wrapContentWidth()
                                                                    .fillMaxHeight()
                                                            )
                                                        }

                                                        is ApiResult.Error -> item {
                                                            Text(
                                                                modifier = Modifier
                                                                    .alpha(0.5f)
                                                                    .padding(horizontal = 12.dp),
                                                                text = stringResource(R.string.reviews_load_error),
                                                                textAlign = TextAlign.Center,
                                                                style = MaterialTheme.typography.bodyMedium,
                                                                letterSpacing = TextUnit(
                                                                    1.5f,
                                                                    TextUnitType.Sp
                                                                ),
                                                                fontWeight = FontWeight.W400
                                                            )
                                                        }

                                                        is ApiResult.Succeed -> {
                                                            if (state.reviews.data.isNullOrEmpty())
                                                                item {
                                                                    Text(
                                                                        modifier = Modifier
                                                                            .alpha(0.5f)
                                                                            .padding(horizontal = 12.dp),
                                                                        text = stringResource(R.string.no_reviews),
                                                                        textAlign = TextAlign.Center,
                                                                        style = MaterialTheme.typography.bodyMedium,
                                                                        letterSpacing = TextUnit(
                                                                            1.5f,
                                                                            TextUnitType.Sp
                                                                        ),
                                                                        fontWeight = FontWeight.W400
                                                                    )
                                                                }
                                                            else {
                                                                item {
                                                                    Row(
                                                                        modifier = Modifier
                                                                            .fillMaxWidth()
                                                                            .padding(
                                                                                horizontal = 12.dp,
                                                                                vertical = 8.dp
                                                                            ),
                                                                        verticalAlignment = Alignment.CenterVertically,
                                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                                    ) {
                                                                        TextButton(
                                                                            modifier = Modifier
                                                                                .weight(1f)
                                                                                .padding(horizontal = 8.dp),
                                                                            onClick = {
                                                                                onGoToReviews(
                                                                                    state.recipe.data.recipeID
                                                                                )
                                                                            },
                                                                            shape = CircleShape
                                                                        ) {
                                                                            Text(
                                                                                text = stringResource(
                                                                                    R.string.go_to_reviews_label
                                                                                ),
                                                                                textAlign = TextAlign.Center,
                                                                                style = MaterialTheme.typography.titleLarge,
                                                                                letterSpacing = TextUnit(
                                                                                    1.5f,
                                                                                    TextUnitType.Sp
                                                                                ),
                                                                                fontWeight = FontWeight.W500
                                                                            )
                                                                        }
                                                                        if (!state.own)
                                                                            IconButton(onClick = {
                                                                                onGoToReviews(
                                                                                    state.recipe.data.recipeID
                                                                                )
                                                                            }) {
                                                                                Image(
                                                                                    painter = painterResource(
                                                                                        R.drawable.comment_add_ic
                                                                                    ),
                                                                                    contentDescription = ""
                                                                                )
                                                                            }
                                                                    }
                                                                }
                                                                items(state.reviews.data) {
                                                                    ReviewCard(
                                                                        modifier = Modifier
                                                                            .fillMaxWidth()
                                                                            .padding(4.dp)
                                                                            .border(
                                                                                BorderStroke(
                                                                                    1.5.dp,
                                                                                    Color.Gray
                                                                                ),
                                                                                shape = RoundedCornerShape(
                                                                                    16.dp
                                                                                )
                                                                            )
                                                                            .padding(8.dp),
                                                                        review = it
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
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun ReceiptPagePreview() {
    RecipeSharing_theme {
        Surface {
            var state by remember {
                mutableStateOf(
                    RecipePageState(
                        recipe = ApiResult.Succeed(
                            Recipe(
                                recipeID = "",
                                creatorID = "",
                                imageUrl = "",
                                recipeName = "Extra mayonnaise",
                                description = "Lorem ipsum dolor sit amet",
                                ingredients = listOf(
                                    Ingredient("Mayonaise1", 100f, Measure.Milliliter),
                                    Ingredient("Mayonaise2", 100f, Measure.Milliliter),
                                    Ingredient("Mayonaise3", 100f, Measure.Milliliter),
                                    Ingredient("Mayonaise4", 100f, Measure.Milliliter)
                                ),
                                steps = listOf(
                                    Step(
                                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse sit amet est varius, tempor tortor non, pellentesque mi. Praesent accumsan facilisis urna nec semper. Proin gravida consectetur augue. Nullam pharetra nulla at malesuada consequat. Donec eu tortor vitae risus laoreet mollis nec in ipsum. Donec sem erat, rhoncus a iaculis at, accumsan eget nisl. Nulla hendrerit dui in quam rutrum, id ultricies urna facilisis. Fusce urna augue, maximus at tortor pellentesque, laoreet auctor tortor. Maecenas ut eros enim. Donec faucibus venenatis semper. Pellentesque laoreet metus blandit arcu venenatis auctor ac non arcu.\n" +
                                                "\n" +
                                                "Phasellus nulla leo, condimentum in est et, ornare tincidunt neque. Morbi lectus velit, cursus quis pharetra sed, semper rhoncus felis. Pellentesque volutpat ipsum vitae mattis sodales. Proin mattis nulla velit, ac venenatis nisi euismod ut. Sed non imperdiet neque. Sed lacinia libero erat. Vestibulum id pellentesque tellus, at suscipit nulla. Duis ut erat interdum, laoreet nibh ut, lobortis est.",
                                        123123123L
                                    ),
                                    Step(
                                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse sit amet est varius, tempor tortor non, pellentesque mi. Praesent accumsan facilisis urna nec semper. Proin gravida consectetur augue. Nullam pharetra nulla at malesuada consequat. Donec eu tortor vitae risus laoreet mollis nec in ipsum. Donec sem erat, rhoncus a iaculis at, accumsan eget nisl. Nulla hendrerit dui in quam rutrum, id ultricies urna facilisis. Fusce urna augue, maximus at tortor pellentesque, laoreet auctor tortor. Maecenas ut eros enim. Donec faucibus venenatis semper. Pellentesque laoreet metus blandit arcu venenatis auctor ac non arcu.\n" +
                                                "\n" +
                                                "Phasellus nulla leo, condimentum in est et, ornare tincidunt neque. Morbi lectus velit, cursus quis pharetra sed, semper rhoncus felis. Pellentesque volutpat ipsum vitae mattis sodales. Proin mattis nulla velit, ac venenatis nisi euismod ut. Sed non imperdiet neque. Sed lacinia libero erat. Vestibulum id pellentesque tellus, at suscipit nulla. Duis ut erat interdum, laoreet nibh ut, lobortis est.",
                                        123123123L
                                    ),
                                    Step(
                                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse sit amet est varius, tempor tortor non, pellentesque mi. Praesent accumsan facilisis urna nec semper. Proin gravida consectetur augue. Nullam pharetra nulla at malesuada consequat. Donec eu tortor vitae risus laoreet mollis nec in ipsum. Donec sem erat, rhoncus a iaculis at, accumsan eget nisl. Nulla hendrerit dui in quam rutrum, id ultricies urna facilisis. Fusce urna augue, maximus at tortor pellentesque, laoreet auctor tortor. Maecenas ut eros enim. Donec faucibus venenatis semper. Pellentesque laoreet metus blandit arcu venenatis auctor ac non arcu.\n" +
                                                "\n" +
                                                "Phasellus nulla leo, condimentum in est et, ornare tincidunt neque. Morbi lectus velit, cursus quis pharetra sed, semper rhoncus felis. Pellentesque volutpat ipsum vitae mattis sodales. Proin mattis nulla velit, ac venenatis nisi euismod ut. Sed non imperdiet neque. Sed lacinia libero erat. Vestibulum id pellentesque tellus, at suscipit nulla. Duis ut erat interdum, laoreet nibh ut, lobortis est.",
                                        123123123L
                                    ),
                                    Step(
                                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse sit amet est varius, tempor tortor non, pellentesque mi. Praesent accumsan facilisis urna nec semper. Proin gravida consectetur augue. Nullam pharetra nulla at malesuada consequat. Donec eu tortor vitae risus laoreet mollis nec in ipsum. Donec sem erat, rhoncus a iaculis at, accumsan eget nisl. Nulla hendrerit dui in quam rutrum, id ultricies urna facilisis. Fusce urna augue, maximus at tortor pellentesque, laoreet auctor tortor. Maecenas ut eros enim. Donec faucibus venenatis semper. Pellentesque laoreet metus blandit arcu venenatis auctor ac non arcu.\n" +
                                                "\n" +
                                                "Phasellus nulla leo, condimentum in est et, ornare tincidunt neque. Morbi lectus velit, cursus quis pharetra sed, semper rhoncus felis. Pellentesque volutpat ipsum vitae mattis sodales. Proin mattis nulla velit, ac venenatis nisi euismod ut. Sed non imperdiet neque. Sed lacinia libero erat. Vestibulum id pellentesque tellus, at suscipit nulla. Duis ut erat interdum, laoreet nibh ut, lobortis est.",
                                        123123123L
                                    ),
                                ),
                                reviewsCount = 100_000_000,
                                currentRating = 0f,
                                viewsCount = 0L
                            )
                        ),
                        ingredients = listOf(
                            Ingredient("Mayonaise1", 100f, Measure.Milliliter),
                            Ingredient("Mayonaise2", 100f, Measure.Milliliter),
                            Ingredient("Mayonaise3", 100f, Measure.Milliliter),
                            Ingredient("Mayonaise4", 100f, Measure.Milliliter)
                        ),
                        steps = listOf(
                            Step(
                                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse sit amet est varius, tempor tortor non, pellentesque mi. Praesent accumsan facilisis urna nec semper. Proin gravida consectetur augue. Nullam pharetra nulla at malesuada consequat. Donec eu tortor vitae risus laoreet mollis nec in ipsum. Donec sem erat, rhoncus a iaculis at, accumsan eget nisl. Nulla hendrerit dui in quam rutrum, id ultricies urna facilisis. Fusce urna augue, maximus at tortor pellentesque, laoreet auctor tortor. Maecenas ut eros enim. Donec faucibus venenatis semper. Pellentesque laoreet metus blandit arcu venenatis auctor ac non arcu.\n" +
                                        "\n" +
                                        "Phasellus nulla leo, condimentum in est et, ornare tincidunt neque. Morbi lectus velit, cursus quis pharetra sed, semper rhoncus felis. Pellentesque volutpat ipsum vitae mattis sodales. Proin mattis nulla velit, ac venenatis nisi euismod ut. Sed non imperdiet neque. Sed lacinia libero erat. Vestibulum id pellentesque tellus, at suscipit nulla. Duis ut erat interdum, laoreet nibh ut, lobortis est.",
                                123123123L
                            ),
                            Step(
                                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse sit amet est varius, tempor tortor non, pellentesque mi. Praesent accumsan facilisis urna nec semper. Proin gravida consectetur augue. Nullam pharetra nulla at malesuada consequat. Donec eu tortor vitae risus laoreet mollis nec in ipsum. Donec sem erat, rhoncus a iaculis at, accumsan eget nisl. Nulla hendrerit dui in quam rutrum, id ultricies urna facilisis. Fusce urna augue, maximus at tortor pellentesque, laoreet auctor tortor. Maecenas ut eros enim. Donec faucibus venenatis semper. Pellentesque laoreet metus blandit arcu venenatis auctor ac non arcu.\n" +
                                        "\n" +
                                        "Phasellus nulla leo, condimentum in est et, ornare tincidunt neque. Morbi lectus velit, cursus quis pharetra sed, semper rhoncus felis. Pellentesque volutpat ipsum vitae mattis sodales. Proin mattis nulla velit, ac venenatis nisi euismod ut. Sed non imperdiet neque. Sed lacinia libero erat. Vestibulum id pellentesque tellus, at suscipit nulla. Duis ut erat interdum, laoreet nibh ut, lobortis est.",
                                123123123L
                            ),
                            Step(
                                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse sit amet est varius, tempor tortor non, pellentesque mi. Praesent accumsan facilisis urna nec semper. Proin gravida consectetur augue. Nullam pharetra nulla at malesuada consequat. Donec eu tortor vitae risus laoreet mollis nec in ipsum. Donec sem erat, rhoncus a iaculis at, accumsan eget nisl. Nulla hendrerit dui in quam rutrum, id ultricies urna facilisis. Fusce urna augue, maximus at tortor pellentesque, laoreet auctor tortor. Maecenas ut eros enim. Donec faucibus venenatis semper. Pellentesque laoreet metus blandit arcu venenatis auctor ac non arcu.\n" +
                                        "\n" +
                                        "Phasellus nulla leo, condimentum in est et, ornare tincidunt neque. Morbi lectus velit, cursus quis pharetra sed, semper rhoncus felis. Pellentesque volutpat ipsum vitae mattis sodales. Proin mattis nulla velit, ac venenatis nisi euismod ut. Sed non imperdiet neque. Sed lacinia libero erat. Vestibulum id pellentesque tellus, at suscipit nulla. Duis ut erat interdum, laoreet nibh ut, lobortis est.",
                                123123123L
                            ),
                            Step(
                                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse sit amet est varius, tempor tortor non, pellentesque mi. Praesent accumsan facilisis urna nec semper. Proin gravida consectetur augue. Nullam pharetra nulla at malesuada consequat. Donec eu tortor vitae risus laoreet mollis nec in ipsum. Donec sem erat, rhoncus a iaculis at, accumsan eget nisl. Nulla hendrerit dui in quam rutrum, id ultricies urna facilisis. Fusce urna augue, maximus at tortor pellentesque, laoreet auctor tortor. Maecenas ut eros enim. Donec faucibus venenatis semper. Pellentesque laoreet metus blandit arcu venenatis auctor ac non arcu.\n" +
                                        "\n" +
                                        "Phasellus nulla leo, condimentum in est et, ornare tincidunt neque. Morbi lectus velit, cursus quis pharetra sed, semper rhoncus felis. Pellentesque volutpat ipsum vitae mattis sodales. Proin mattis nulla velit, ac venenatis nisi euismod ut. Sed non imperdiet neque. Sed lacinia libero erat. Vestibulum id pellentesque tellus, at suscipit nulla. Duis ut erat interdum, laoreet nibh ut, lobortis est.",
                                123123123L
                            ),
                        ),
                        recipeName = "Some test",
                        recipeDescription = "Lorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit amet",
                        filters = ApiResult.Succeed(
                            listOf(
                                "FilterOne",
                                "FilterTwo"
                            )
                        ),
                        own = true,
                        isEditingRecord = false,
                        recipeRating = 3.5f,
                        recipeReviewsCount = 1_000_000_000,
                        selectedRecipeTabIndex = 0,
                        reviews = ApiResult.Succeed(
                            listOf(
                                ReviewModel(
                                    "",
                                    "Some userName",
                                    "",
                                    "Not so good",
                                    3
                                ),
                                ReviewModel(
                                    "",
                                    "Some userName",
                                    "",
                                    "Not so good",
                                    3
                                ),
                                ReviewModel(
                                    "",
                                    "Some userName",
                                    "",
                                    "Not so good",
                                    3
                                ),
                                ReviewModel(
                                    "",
                                    "Some userName",
                                    "",
                                    "Not so good",
                                    3
                                ),
                                ReviewModel(
                                    "",
                                    "Some userName",
                                    "",
                                    "Not so good",
                                    3
                                ),
                                ReviewModel(
                                    "",
                                    "Some userName",
                                    "",
                                    "Not so good",
                                    3
                                ),
                            )
                        ),
                        loadedFilters = ApiResult.Succeed(
                            mapOf(
                                "Cat1" to listOf("Fil1", "Fil1", "Fil2"),
                                "Cat2" to listOf("Fil1", "Fil1", "Fil2"),
                                "Cat3" to listOf("Fil1", "Fil1", "Fil2"),
                            )
                        )
                    )
                )
            }
            SharedTransitionLayout {
                AnimatedVisibility(true) {
                    RecipeConfigPage(modifier = Modifier
                        .fillMaxSize(),
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibility = this,
                        state = state,
                        onEvent = {
                            if (it is RecipePageEvent.EditRecord)
                                state = state.copy(isEditingRecord = true)
                            else if (it is RecipePageEvent.DiscardChanges || it is RecipePageEvent.SaveChanges)
                                state = state.copy(isEditingRecord = false)
                            else if (it is RecipePageEvent.SetSelectedRecipeTabIndex)
                                state = state.copy(selectedRecipeTabIndex = it.recipeTabIndex)
                            else if (it is RecipePageEvent.SetIsError)
                                state = state.copy(isError = it.isError)
                            else if (it is RecipePageEvent.CloseDialogs)
                                state = state.copy(
                                    openAddIngredientDialog = false,
                                    openAddStepDialog = false
                                )
                            else if (it is RecipePageEvent.SetOpenFiltersPage)
                                state = state.copy(openFiltersPage = it.openDialog)
                        },
                        onGoBack = {},
                        onReloadData = {},
                        onGoToReviews = {},
                        onGoToPostReview = {})
                }
            }

        }
    }
}
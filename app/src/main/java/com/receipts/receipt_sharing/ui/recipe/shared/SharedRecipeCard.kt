package com.receipts.receipt_sharing.ui.recipe.shared

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
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
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.recipes.RecipeDifficulty
import com.receipts.receipt_sharing.presentation.RecipeSharedElementKey
import com.receipts.receipt_sharing.presentation.RecipeSharedElementType
import com.receipts.receipt_sharing.ui.recipe.elements.DifficultyMeter
import com.receipts.receipt_sharing.ui.recipe.elements.RatingRow
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme
import kotlin.random.Random

/**
 * Composes Recipe card
 * @param recipe Recipe information
 * @param modifier Modifier applied to recipe
 */
@OptIn(
    ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun RecipeCard(
    modifier: Modifier = Modifier,
    recipe: Recipe,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibility: AnimatedVisibilityScope,
) {
    var openDescription by remember {
        mutableStateOf(false)
    }
    val localDensity = LocalDensity.current
    var starsSize by remember {
        mutableStateOf(32.dp)
    }
    val starSizePx = remember(starsSize) { with(localDensity) { starsSize.toPx() + 8.dp.toPx() } }

    var meterRadius by remember {
        mutableStateOf(48.dp)
    }
    val meterRadiusPx = remember(meterRadius) {
        with(localDensity) { meterRadius.toPx() }
    }

    val viewsSizePx = remember(recipe.viewsCount.toAmountString().length) {
        with(localDensity) {
            Offset(
                y = 24.dp.toPx(),
                x = 24.dp.toPx() + (6.75 * (recipe.viewsCount.toAmountString().length + 2)).dp.toPx()
            )
        }
    }

    val scrollState = rememberScrollState(0)
    with(sharedTransitionScope) {
        Card(
            modifier = Modifier
                .sharedBounds(
                    rememberSharedContentState(
                        key = RecipeSharedElementKey(
                            id = recipe.recipeID,
                            origin = "recipes",
                            type = RecipeSharedElementType.Bounds
                        )
                    ),
                    animatedVisibilityScope = animatedVisibility
                )
                .then(modifier)
                .onGloballyPositioned { coords ->
                    if (coords.size.width.toFloat() < starSizePx * 5f)
                        starsSize *= (coords.size.width.toFloat() * 0.8f) / (starSizePx * 5f)
                    if ((meterRadiusPx * 2) / coords.size.width.toFloat() > 0.3f)
                        meterRadius *= (coords.size.width.toFloat() * 0.25f) / (meterRadiusPx * 2f)
                },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .sharedElement(
                        rememberSharedContentState(
                            key = RecipeSharedElementKey(
                                id = recipe.recipeID,
                                origin = "recipes",
                                type = RecipeSharedElementType.Image
                            )
                        ),
                        animatedVisibilityScope = animatedVisibility
                    )
            ) {
                if (recipe.imageUrl.isNullOrEmpty())
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .graphicsLayer {
                                compositingStrategy = CompositingStrategy.Offscreen
                            }
                            .drawWithCache {
                                val path = Path()
                                path.addRoundRect(
                                    RoundRect(
                                        Rect(
                                            topLeft = Offset.Zero,
                                            bottomRight = Offset(size.width, size.height)
                                        ),
                                        cornerRadius = CornerRadius(16f, 16f)
                                    )
                                )
                                onDrawWithContent {
                                    clipPath(path) {
                                        this@onDrawWithContent.drawContent()
                                    }
                                    drawRoundRect(
                                        Color.Black,
                                        topLeft = Offset(
                                            x = size.width - viewsSizePx.x,
                                            y = size.height - viewsSizePx.y
                                        ),
                                        blendMode = BlendMode.Clear,
                                        cornerRadius = CornerRadius(
                                            x = 0f,
                                            y = with(localDensity) { 8.dp.toPx() },
                                        )
                                    )
                                    drawCircle(
                                        Color.Black,
                                        radius = meterRadiusPx + 6.dp.toPx(),
                                        center = Offset(
                                            x = (meterRadius + 12.dp).toPx(),
                                            y = (meterRadius + 12.dp).toPx()
                                        ),
                                        blendMode = BlendMode.Clear,
                                    )
                                }
                            },
                        contentScale = ContentScale.Crop,
                        painter = painterResource(R.drawable.no_image), contentDescription = ""
                    )
                else {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                compositingStrategy = CompositingStrategy.Offscreen
                            }
                            .drawWithCache {
                                val path = Path()
                                path.addRoundRect(
                                    RoundRect(
                                        Rect(
                                            topLeft = Offset.Zero,
                                            bottomRight = Offset(size.width, size.height)
                                        ),
                                        cornerRadius = CornerRadius(16f, 16f)
                                    )
                                )
                                onDrawWithContent {
                                    clipPath(path) {
                                        this@onDrawWithContent.drawContent()
                                    }
                                    drawRoundRect(
                                        Color.Black,
                                        topLeft = Offset(
                                            x = size.width - viewsSizePx.x,
                                            y = size.height - viewsSizePx.y
                                        ),
                                        blendMode = BlendMode.Clear,
                                        cornerRadius = CornerRadius(
                                            x = 0f,
                                            y = with(localDensity) { 8.dp.toPx() },
                                        )
                                    )
                                    drawCircle(
                                        Color.Black,
                                        radius = meterRadiusPx + 6.dp.toPx(),
                                        center = Offset(
                                            x = (meterRadius + 12.dp).toPx(),
                                            y = (meterRadius + 12.dp).toPx()
                                        ),
                                        blendMode = BlendMode.Clear,
                                    )
                                }
                            },
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(recipe.imageUrl)
                            .crossfade(true)
                            .build(),
                        imageLoader = UnsafeImageLoader.getInstance(),
                        contentScale = ContentScale.Fit,
                        contentDescription = "",
                    )
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 12.dp, start = 12.dp)
                ) {
                    DifficultyMeter(
                        modifier = Modifier
                            .size(meterRadius * 2),
                        difficulty = recipe.difficulty
                    )
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {

                    Icon(
                        painter = painterResource(R.drawable.view_ic),
                        contentDescription = null
                    )
                    Text(
                        text = "(${recipe.viewsCount.toAmountString()})",
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.bodyMedium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        fontWeight = FontWeight.W400
                    )
                }
            }
            FlowRow(
                horizontalArrangement = Arrangement.Start
            ) {

                RatingRow(
                    currentRating = recipe.currentRating,
                    starSize = starsSize
                )
                Text(
                    text = "(${recipe.reviewsCount.toAmountString()})",
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontWeight = FontWeight.W400
                )
            }
            Text(
                modifier = Modifier
                    .padding(start = 16.dp, top = 12.dp, bottom = 8.dp)
                    .sharedElement(
                        rememberSharedContentState(
                            key = RecipeSharedElementKey(
                                id = recipe.recipeID,
                                origin = "recipes",
                                type = RecipeSharedElementType.Title
                            )
                        ),
                        animatedVisibilityScope = animatedVisibility
                    )
                    .skipToLookaheadSize()
                    .align(Alignment.Start)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { openDescription = !openDescription }
                        )
                    },
                style = MaterialTheme.typography.titleLarge,
                text = recipe.recipeName,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                letterSpacing = TextUnit(0.15f, TextUnitType.Em),
                fontWeight = FontWeight.W500
            )
            AnimatedVisibility(
                openDescription,
                enter = slideInVertically()
                        + expandVertically(expandFrom = Alignment.Bottom)
                        + fadeIn(),
                exit = fadeOut(targetAlpha = 0.0f)
                        + slideOutVertically()
                        + shrinkVertically(shrinkTowards = Alignment.Top)
            ) {
                Text(
                    modifier = Modifier
                        .height(68.dp)
                        .padding(vertical = 4.dp, horizontal = 4.dp)
                        .sharedElement(
                            rememberSharedContentState(
                                key = RecipeSharedElementKey(
                                    id = recipe.recipeID,
                                    origin = "recipes",
                                    type = RecipeSharedElementType.Description
                                )
                            ),
                            animatedVisibilityScope = animatedVisibility
                        )
                        .skipToLookaheadSize()
                        .verticalScroll(scrollState),
                    text = recipe.description ?: stringResource(R.string.no_description),
                    style = MaterialTheme.typography.bodyLarge,
                    letterSpacing = TextUnit(1.5f, TextUnitType.Sp)
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun ReceiptCardPreview() {
    RecipeSharing_theme(darkTheme = true) {
        var openDialog by remember {
            mutableStateOf(false)
        }
        SharedTransitionLayout {
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(
                        modifier = Modifier,
                        onClick = { /*TODO*/ },
                        shape = CircleShape
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "")
                    }
                }
            )
            {
                LazyVerticalStaggeredGrid(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxWidth(),
                    columns = StaggeredGridCells.Fixed(1),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (openDialog)
                        item {
                            Image(
                                painter = painterResource(R.drawable.ic_launcher_background),
                                contentDescription = ""
                            )
                        }
                    items(7) {
                        AnimatedVisibility(true) {
                            RecipeCard(
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .clickable { openDialog = !openDialog },
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedVisibility = this@AnimatedVisibility,
                                recipe = Recipe(
                                    "$it",
                                    "",
                                    "",
                                    "afhoaiehfa[oisehf[oaiuhf[oihfiEHF[OIefh[oiHF[OIWhf[oiho[iahgr[iohg[oirhg[oisrha[ioghaoihfriovaho[ivfshovivoihseovihsvhsoivhsoivhs",
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
                                    viewsCount = 100L,
                                    difficulty = RecipeDifficulty.entries[Random.nextInt(4)]
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
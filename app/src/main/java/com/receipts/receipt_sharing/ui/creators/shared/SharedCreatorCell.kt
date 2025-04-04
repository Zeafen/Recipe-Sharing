package com.receipts.receipt_sharing.ui.creators.shared

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.receipts.receipt_sharing.data.helpers.toAmountString
import com.receipts.receipt_sharing.data.helpers.UnsafeImageLoader
import com.receipts.receipt_sharing.domain.creators.CreatorRequest
import com.receipts.receipt_sharing.presentation.RecipeSharedElementKey
import com.receipts.receipt_sharing.presentation.RecipeSharedElementType
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme


/**
 * Composes creator cell (card)
 * @param modifier Modifier applied to Card
 * @param creator Creator info
 */
@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalLayoutApi::class)
@Composable
fun CreatorCell(
    modifier: Modifier = Modifier,
    creator: CreatorRequest,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibility: AnimatedVisibilityScope
) {
    with(sharedTransitionScope) {
        Card(
            modifier = Modifier
                .sharedBounds(
                    rememberSharedContentState(
                        key = RecipeSharedElementKey(
                            id = creator.userID,
                            origin = "creators",
                            type = RecipeSharedElementType.Bounds
                        )
                    ),
                    animatedVisibilityScope = animatedVisibility
                )
                .then(modifier),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (creator.imageUrl.isBlank())
                Image(
                    modifier = Modifier
                        .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
                        .sharedElement(
                            rememberSharedContentState(
                                key = RecipeSharedElementKey(
                                    id = creator.userID,
                                    origin = "creators",
                                    type = RecipeSharedElementType.Image
                                )
                            ),
                            animatedVisibilityScope = animatedVisibility
                        )
                        .fillMaxWidth()
                        .wrapContentWidth()
                        .clip(CircleShape)
                        .heightIn(64.dp, 128.dp)
                        .widthIn(64.dp, 128.dp),
                    contentScale = ContentScale.Crop,
                    painter = painterResource(R.drawable.person_ic),
                    contentDescription = ""
                )
            else
                AsyncImage(
                    modifier = Modifier
                        .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
                        .sharedElement(
                            rememberSharedContentState(
                                key = RecipeSharedElementKey(
                                    id = creator.userID,
                                    origin = "creators",
                                    type = RecipeSharedElementType.Image
                                )
                            ),
                            animatedVisibilityScope = animatedVisibility
                        )
                        .fillMaxWidth()
                        .wrapContentWidth()
                        .clip(CircleShape)
                        .heightIn(64.dp, 128.dp)
                        .widthIn(64.dp, 128.dp),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(creator.imageUrl)
                        .crossfade(true)
                        .build(),
                    imageLoader = UnsafeImageLoader.getInstance(),
                    contentScale = ContentScale.Fit,
                    contentDescription = ""
                )
            Text(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .sharedElement(
                        rememberSharedContentState(
                            key = RecipeSharedElementKey(
                                id = creator.userID,
                                origin = "creators",
                                type = RecipeSharedElementType.Title
                            )
                        ),
                        animatedVisibilityScope = animatedVisibility
                    )
                    .fillMaxWidth(1f),
                textAlign = TextAlign.Justify,
                text = creator.nickname,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge,
                letterSpacing = TextUnit(0.15f, TextUnitType.Em),
                fontWeight = FontWeight.W400
            )
            FlowRow(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ){
                Text(
                    modifier = Modifier
                        .alpha(0.5f)
                        .padding(end = 8.dp),
                    textAlign = TextAlign.Start,
                    text = stringResource(
                        R.string.creator_recipes_count,
                        creator.recipesCount.toAmountString()
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    modifier = Modifier
                        .alpha(0.5f),
                    textAlign = TextAlign.Start,
                    text = stringResource(
                        R.string.followers_count,
                        creator.followersCount.toAmountString()
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun Preview() {
    RecipeSharing_theme(darkTheme = true) {
        Surface {
            SharedTransitionLayout {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(1),
                    verticalItemSpacing = 12.dp
                ) {
                    items(10) {
                        com.receipts.receipt_sharing.ui.creators.CreatorCell(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            creator = CreatorRequest(
                                "",
                                "New artist New artist New artist New artist New artist New artist New artist",
                                "",
                                followersCount = 1_000L,
                                recipesCount = 123_456_000L,
                                imageUrl = ""
                            )
                        )
                    }
                }
            }
        }
    }
}
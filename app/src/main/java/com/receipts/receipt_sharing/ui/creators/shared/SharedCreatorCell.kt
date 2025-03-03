package com.receipts.receipt_sharing.ui.creators.shared

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.receipts.receipt_sharing.data.helpers.toAmountString
import com.receipts.receipt_sharing.domain.creators.CreatorRequest
import com.receipts.receipt_sharing.domain.apiServices.UnsafeImageLoader
import com.receipts.receipt_sharing.presentation.RecipeSharedElementKey
import com.receipts.receipt_sharing.presentation.RecipeSharedElementType
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CreatorCell(
    modifier: Modifier = Modifier,
    creator: CreatorRequest,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibility: AnimatedVisibilityScope
) {
    with(sharedTransitionScope) {
        Column(
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
            horizontalAlignment = Alignment.CenterHorizontally,
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
                    .padding(horizontal = 8.dp)
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
                    .fillMaxWidth(),
                textAlign = TextAlign.Justify,
                text = creator.nickname,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge,
                letterSpacing = TextUnit(0.15f, TextUnitType.Em),
                fontWeight = FontWeight.W400
            )
            Text(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .alpha(0.5f),
                textAlign = TextAlign.Start,
                text = stringResource(R.string.creator_recipes_count, creator.recipesCount.toAmountString()),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .alpha(0.5f),
                textAlign = TextAlign.Start,
                text = stringResource(R.string.followers_count, creator.followersCount.toAmountString()),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun Preview() {
    RecipeSharing_theme {
        Surface {
            SharedTransitionLayout {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
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
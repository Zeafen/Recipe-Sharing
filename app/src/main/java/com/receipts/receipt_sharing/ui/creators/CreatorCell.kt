package com.receipts.receipt_sharing.ui.creators

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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.data.helpers.toAmountString
import com.receipts.receipt_sharing.domain.creators.CreatorRequest
import com.receipts.receipt_sharing.domain.apiServices.UnsafeImageLoader
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme

@Composable
fun CreatorCell(
    modifier: Modifier = Modifier,
    creator: CreatorRequest
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        if(creator.imageUrl.isBlank())
            Image(
                modifier = Modifier
                    .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
                    .fillMaxWidth()
                    .wrapContentWidth()
                    .clip(CircleShape)
                    .heightIn(64.dp, 128.dp)
                    .widthIn(64.dp, 128.dp),
                contentScale = ContentScale.Crop,
                painter = painterResource(R.drawable.person_ic),
                contentDescription = "")
        else
            AsyncImage(
                modifier = Modifier
                    .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
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
                contentDescription = "")
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Justify,
            text = creator.nickname,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.W400,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
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

@Preview
@Composable
private fun Preview() {
    RecipeSharing_theme {
        Surface {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                verticalItemSpacing = 12.dp
            ) {
                items(10) {
                    CreatorCell(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        creator = CreatorRequest(
                            "",
                            "New artist New artist New artist New artist New artist New artist New artist",
                            "",
                            ""
                        )
                    )
                }
            }
        }
    }
}
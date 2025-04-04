package com.receipts.receipt_sharing.ui.reviews

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.data.helpers.UnsafeImageLoader
import com.receipts.receipt_sharing.domain.reviews.ReviewModel
import com.receipts.receipt_sharing.ui.recipe.RatingRow
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * Composes view-only review card
 * @param modifier Modifier applied to the ReviewCard
 * @param review review information
 */
@Composable
fun ReviewCard(
    modifier: Modifier = Modifier,
    review: ReviewModel
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .padding(bottom = 8.dp, start = 8.dp, end = 4.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (review.userImageUrl.isBlank())
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
                        .data(review.userImageUrl)
                        .crossfade(true)
                        .build(),
                    imageLoader = UnsafeImageLoader.getInstance(),
                    contentDescription = ""
                )
            Text(
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                text = review.userName,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                letterSpacing = TextUnit(
                    1.5f,
                    TextUnitType.Sp
                ),
                fontWeight = FontWeight.W500
            )
        }
        RatingRow(modifier = Modifier
            .padding(bottom = 8.dp, start = 4.dp),
            starSize = 24.dp,
            currentRating = review.rating.toFloat())
        Text(modifier = Modifier
            .padding(bottom = 8.dp, start = 12.dp, end = 8.dp),
            text = review.text,
            textAlign = TextAlign.Justify,
            style = MaterialTheme.typography.bodyLarge,
            letterSpacing = TextUnit(
                1.5f,
                TextUnitType.Sp
            ),
            fontWeight = FontWeight.W400
        )
    }
}

/**
 * Composes editable review card
 * @param modifier Modifier applied to the ReviewCard
 * @param review review information
 * @param onEditClick called when user click on "Edit" button
 * @param onDeleteClick called when user click on "Delete button"
 */
@Composable
fun ReviewCard(
    modifier: Modifier = Modifier,
    review: ReviewModel,
    onEditClick : () -> Unit,
    onDeleteClick : () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, start = 8.dp, end = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (review.userImageUrl.isBlank())
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
                        .data(review.userImageUrl)
                        .crossfade(true)
                        .build(),
                    imageLoader = UnsafeImageLoader.getInstance(),
                    contentDescription = ""
                )
            Text(
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                text = review.userName,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                letterSpacing = TextUnit(
                    1.5f,
                    TextUnitType.Sp
                ),
                fontWeight = FontWeight.W500
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                IconButton(onClick = onEditClick) {
                    Icon(
                        painter = painterResource(R.drawable.edit_ic),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
        RatingRow(modifier = Modifier
            .padding(bottom = 8.dp, start = 4.dp),
            starSize = 24.dp,
            currentRating = review.rating.toFloat())
        Text(modifier = Modifier
            .padding(bottom = 8.dp, start = 12.dp, end = 8.dp),
            text = review.text,
            textAlign = TextAlign.Justify,
            style = MaterialTheme.typography.bodyLarge,
            letterSpacing = TextUnit(
                1.5f,
                TextUnitType.Sp
            ),
            fontWeight = FontWeight.W400
        )
    }
}

@Preview
@Composable
private fun Preview() {
    RecipeSharing_theme {
        Surface {
            LazyColumn(Modifier.fillMaxSize()) {
                items(10) {
                    val text = "SomeNewText".repeat((it + 1) * 10)
                    ReviewCard(
                        modifier = Modifier
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        review = ReviewModel(
                            id = "",
                            userName = "Username $it",
                            userImageUrl = "",
                            text = text,
                            rating = Random.nextInt(IntRange(0, 5))
                        ),
                    )
                }
            }
        }
    }
}
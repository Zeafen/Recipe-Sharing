package com.receipts.receipt_sharing.ui.creators

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.domain.CreatorRequest
import com.receipts.receipt_sharing.domain.apiServices.UnsafeImageLoader
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme

@Composable
fun CreatorCell(modifier: Modifier = Modifier,
                creator : CreatorRequest
) {
    Row(modifier = modifier) {
        if(creator.imageUrl.isBlank())
            Image(modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 4.dp)
                .size(50.dp)
                .clip(CircleShape),
                contentScale = ContentScale.Crop,
                painter = painterResource(R.drawable.no_image),
                contentDescription = "")
        else
            AsyncImage(modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 8.dp)
                .size(50.dp)
                .clip(CircleShape),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(creator.imageUrl)
                    .crossfade(true)
                    .build(),
                imageLoader = UnsafeImageLoader.getInstance(),
                contentScale = ContentScale.Fit,
                contentDescription = "")
        Text(modifier = Modifier
            .padding(horizontal = 8.dp)
            .align(Alignment.CenterVertically)
            .weight(2f),
            textAlign = TextAlign.Start,
            text = creator.nickname,
            style = MaterialTheme.typography.titleLarge)
    }
}

@Preview
@Composable
private fun Preview() {
    RecipeSharing_theme {
        Surface {
            Column {
                repeat(10) {
                    CreatorCell(modifier = Modifier
                        .fillMaxWidth(),
                        creator = CreatorRequest("","New artist New artist New artist New artist New artist New artist New artist","")
                    )
                }
            }
        }
    }
}
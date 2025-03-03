package com.receipts.receipt_sharing.ui.reviews

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.domain.reviews.RecipeReview
import com.receipts.receipt_sharing.ui.recipe.RatingRow
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme

@Composable
fun ConfigureReviewDialog(
    review: RecipeReview,
    onDismissRequest: () -> Unit,
    onSaveChanges: (RecipeReview) -> Unit
) {
    var reviewState by remember {
        mutableStateOf(review)
    }
    var isError by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(reviewState) {
        isError =
            reviewState.text.length < 100 && reviewState.text.split(" ").size < 20 || reviewState.rating !in 1..5
    }
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            decorFitsSystemWindows = true
        )
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(vertical = 24.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.comment_add_ic),
                    contentDescription = null
                )
                Text(
                    modifier = Modifier
                        .padding(start = 8.dp),
                    text = stringResource(R.string.review_configure_dialog_lbl),
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyLarge,
                    overflow = TextOverflow.Ellipsis,
                    letterSpacing = TextUnit(
                        2f,
                        TextUnitType.Sp
                    ),
                    fontWeight = FontWeight.W400
                )
            }
            RatingRow(
                modifier = Modifier
                    .padding(vertical = 8.dp),
                currentRating = reviewState.rating,
                onStarClick = { rating -> reviewState = reviewState.copy(rating = rating) },
                starSize = 24.dp
            )
            OutlinedTextField(modifier = Modifier
                .fillMaxWidth()
                .height(256.dp)
                .padding(vertical = 8.dp, horizontal = 12.dp),
                value = reviewState.text,
                onValueChange = { reviewState = reviewState.copy(text = it) },
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
                isError = (reviewState.text.length < 100 || reviewState.text.split(" ").size < 20),
                supportingText = {
                    if (reviewState.text.isEmpty())
                        Text(
                            text = stringResource(R.string.empty_field_error),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.W400,
                            color = MaterialTheme.colorScheme.error
                        )
                    else if (reviewState.text.length < 100)
                        Text(
                            text = stringResource(R.string.incorrect_length_least_error, 100),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.W400,
                            color = MaterialTheme.colorScheme.error
                        )
                    else if (reviewState.text.split(" ").size < 20)
                        Text(
                            text = stringResource(R.string.incorrect_words_least_error, 20),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.W400,
                            color = MaterialTheme.colorScheme.error
                        )
                }
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    onClick = onDismissRequest,
                    shape = CircleShape
                ) {
                    Text(
                        text = stringResource(R.string.cancel_changes_str),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        letterSpacing = TextUnit(
                            1.5f,
                            TextUnitType.Sp
                        ),
                        fontWeight = FontWeight.W500
                    )
                }
                TextButton(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    onClick = {
                        onSaveChanges(reviewState)
                        onDismissRequest()
                    },
                    shape = CircleShape,
                    enabled = !isError
                ) {
                    Text(
                        text = stringResource(R.string.save_changes_str),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        letterSpacing = TextUnit(
                            1.5f,
                            TextUnitType.Sp
                        ),
                        fontWeight = FontWeight.W500
                    )
                }
            }
        }
    }
}


@Preview
@Composable
private fun Preview() {
    RecipeSharing_theme(darkTheme = true) {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                repeat(20) {
                    Text(
                        text = stringResource(R.string.empty_field_error),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.W400,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                ConfigureReviewDialog(
                    onDismissRequest = {},
                    review = RecipeReview(
                        "",
                        "",
                        "",
                        "sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsdsdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd sdsd",
                        0
                    )
                ) {

                }
            }
        }
    }
}
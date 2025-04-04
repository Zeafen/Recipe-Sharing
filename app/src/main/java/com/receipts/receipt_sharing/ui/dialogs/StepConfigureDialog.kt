package com.receipts.receipt_sharing.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.text.isDigitsOnly
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.domain.recipes.Step
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme
import okhttp3.internal.toLongOrDefault

/**
 * Composes email editing dialog
 * @param step initial step value
 * @param onSaveChanges called when user click on "Confirm" button
 * @param onDismissRequest called when user tries to dismiss dialog
 */
@Composable
fun StepConfigureDialog(
    onDismissRequest: () -> Unit,
    onSaveChanges: (Step) -> Unit,
    step: Step = Step("", 0)
) {
    Dialog(onDismissRequest = onDismissRequest) {
        var stepState by remember {
            mutableStateOf(step)
        }
        var duration by rememberSaveable {
            mutableStateOf(step.duration.toString())
        }
        val isError = remember(duration) {
            duration.length !in 1..5 || !duration.isDigitsOnly() || duration.toLongOrDefault(0) <= 0
        }

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clip(RoundedCornerShape(16.dp)),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .drawWithContent {
                        drawContent()
                        val contentSize = drawContext.size
                        drawLine(
                            brush = Brush.linearGradient(
                                listOf(
                                    Color.DarkGray,
                                    Color.LightGray
                                )
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(contentSize.width, 0f),
                            strokeWidth = 5f
                        )
                        drawLine(
                            brush = Brush.linearGradient(
                                listOf(
                                    Color.DarkGray,
                                    Color.LightGray
                                )
                            ),
                            start = Offset(0f, contentSize.height),
                            end = Offset(contentSize.width, contentSize.height),
                            strokeWidth = 5f
                        )
                    },
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.step_configure_dialog_header)
            )
            OutlinedTextField(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                label = { Text(text = stringResource(R.string.step_description_str)) },
                value = stepState.description,
                isError = stepState.description.isEmpty(),
                supportingText = {
                    AnimatedVisibility(stepState.description.isEmpty()) {
                        Text(
                            text = stringResource(R.string.empty_field_error),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.W400,
                            color = MaterialTheme.colorScheme.error
                        )

                    }
                },
                maxLines = 2,
                onValueChange = { stepState = stepState.copy(description = it) },
            )

            OutlinedTextField(
                modifier = Modifier
                    .padding(8.dp),
                label = { Text(text = stringResource(R.string.step_duration_str)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                value = duration,
                onValueChange = { duration = it },
                isError = isError,
                supportingText = {
                    AnimatedVisibility(isError) {
                        Text(
                            text = when {
                                duration.isEmpty() -> stringResource(R.string.empty_field_error)

                                duration.length > 5 -> stringResource(
                                    R.string.incorrect_length_range_error,
                                    1,
                                    5
                                )

                                else -> {
                                    stringResource(R.string.illegal_data_format)
                                }
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.W400,
                            color = MaterialTheme.colorScheme.error
                        )

                    }
                })
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 12.dp)
                    .weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    enabled = stepState.description.isNotEmpty() && !isError,
                    onClick = {
                        onSaveChanges(
                            stepState.copy(
                                duration = duration.toLongOrDefault(1L)
                            )
                        )
                    }) {
                    Text(
                        style = MaterialTheme.typography.titleSmall,
                        text = stringResource(id = R.string.confirm_txt)
                    )
                }
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp, vertical = 12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    shape = RoundedCornerShape(16.dp),
                    onClick = onDismissRequest
                ) {
                    Text(
                        style = MaterialTheme.typography.titleSmall,
                        text = stringResource(id = R.string.cancel_txt)
                    )
                }
            }
        }
    }

}


@Preview
@Composable
private fun Preview() {
    RecipeSharing_theme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                StepConfigureDialog(
                    onDismissRequest = { /*TODO*/ },
                    onSaveChanges = {},
                    step = Step(
                        "lkl",
                        12345
                    )
                )
            }
        }
    }
}
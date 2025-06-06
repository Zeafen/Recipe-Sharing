package com.receipts.receipt_sharing.ui.recipe.elements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.presentation.ValidationInfo
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSlider(
    modifier: Modifier = Modifier,
    minTime: Int = 0,
    maxTime: Int = 99999,
    currentTimeFrom: Int,
    currentTimeTo: Int,
    onTimeFromChanged: (Int) -> Unit,
    onTimeToChanged: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    val valueState = remember(currentTimeFrom, currentTimeTo) {
        min(currentTimeFrom, currentTimeTo).toFloat()..max(currentTimeTo, currentTimeFrom).toFloat()
    }
    val rangeState = remember(minTime, maxTime) {
        min(minTime, maxTime).toFloat()..max(minTime, maxTime).toFloat()
    }

    var fromInput by rememberSaveable(currentTimeFrom) {
        mutableStateOf(currentTimeFrom.toString())
    }
    val fromError = remember(fromInput) {
        when {
            fromInput.toIntOrNull() == null -> ValidationInfo(errorInfoID = R.string.illegal_data_format)
            fromInput.toInt() !in minTime..maxTime -> ValidationInfo(
                errorInfoID = R.string.incorrect_length_range_error,
                formatArgs = listOf(minTime, maxTime)
            )

            else -> {
                scope.launch {
                    onTimeFromChanged(fromInput.toInt())
                }
                ValidationInfo(true)
            }
        }
    }

    var toInput by rememberSaveable(currentTimeTo) {
        mutableStateOf(currentTimeTo.toString())
    }
    val toError = remember(toInput) {
        when {
            toInput.toIntOrNull() == null -> ValidationInfo(errorInfoID = R.string.illegal_data_format)
            toInput.toInt() !in minTime..maxTime -> ValidationInfo(
                errorInfoID = R.string.incorrect_length_range_error,
                formatArgs = listOf(minTime, maxTime)
            )

            else -> {
                scope.launch {
                    onTimeToChanged(toInput.toInt())
                }
                ValidationInfo(true)
            }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                ),
                label = {
                    Text(
                        text = stringResource(R.string.from_lbl),
                    )
                },
                value = fromInput,
                onValueChange = { fromInput = it },
                isError = !fromError.isValid,
                supportingText = {
                    AnimatedVisibility(!fromError.isValid) {
                        fromError.errorInfoID?.let {
                            Text(
                                text = stringResource(
                                    it,
                                    *fromError.formatArgs.toTypedArray()
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.W400,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
            OutlinedTextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = {
                    Text(
                        text = stringResource(R.string.to_lbl),
                    )
                },
                supportingText = {
                    AnimatedVisibility(!toError.isValid) {
                        toError.errorInfoID?.let {
                            Text(
                                text = stringResource(
                                    it,
                                    *toError.formatArgs.toTypedArray()
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.W400,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                value = toInput,
                onValueChange = { toInput = it },
                isError = !toError.isValid,
            )
        }
        RangeSlider(
            valueRange = rangeState,
            value = valueState,
            onValueChange = { range ->
                scope.launch {
                    with(Dispatchers.IO) {
                        launch {
                            onTimeToChanged(range.endInclusive.roundToInt())
                        }
                        launch {
                            onTimeFromChanged(range.start.roundToInt())
                        }
                    }
                }
            },
        )
    }
}

@Preview
@Composable
private fun Preview() {
    var timeFrom by remember {
        mutableIntStateOf(200)
    }
    var timeTo by remember {
        mutableIntStateOf(99999)
    }
    RecipeSharing_theme(darkTheme = true) {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                TimeSlider(
                    currentTimeFrom = timeFrom,
                    currentTimeTo = timeTo,
                    onTimeFromChanged = { timeFrom = it },
                    onTimeToChanged = { timeTo = it }
                )
            }
        }
    }
}
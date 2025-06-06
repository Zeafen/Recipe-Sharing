package com.receipts.receipt_sharing.ui.dialogs

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.domain.recipes.Ingredient
import com.receipts.receipt_sharing.domain.recipes.Measure
import com.receipts.receipt_sharing.presentation.ValidationInfo
import com.receipts.receipt_sharing.ui.effects.SwipeableSelection
import com.receipts.receipt_sharing.ui.effects.rememberSelectionState
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme
import kotlin.Float.Companion.MAX_VALUE

/**
 * Composes email editing dialog
 * @param ingredient initial ingredient value
 * @param onSaveChanges called when user click on "Confirm" button
 * @param onDismissRequest called when user tries to dismiss dialog
 */
@Composable
fun IngredientConfigureDialog(
    ingredient: Ingredient = Ingredient("name", 0f, Measure.Gram),
    onDismissRequest: () -> Unit,
    onSaveChanges: (Ingredient) -> Unit
) {
    var ingredientState by remember {
        mutableStateOf(ingredient)
    }
    var amountInput by rememberSaveable {
        mutableStateOf(ingredientState.amount.toString())
    }
    val amountError = remember(amountInput) {
        when {
            amountInput.isEmpty() -> ValidationInfo(false, R.string.empty_field_error)
            amountInput.toFloatOrNull() == null || amountInput.toFloat() !in 1f..MAX_VALUE -> ValidationInfo(
                false,
                R.string.illegal_data_format
            )

            amountInput.split(',', '.').first().length !in 1..5 -> ValidationInfo(
                false,
                R.string.incorrect_length_range_error,
                listOf(1, 5)
            )

            else -> ValidationInfo(true)
        }

    }
    val nameError = remember(ingredientState.name) {
        when {
            ingredientState.name.isEmpty() -> ValidationInfo(false, R.string.empty_field_error)
            !ingredientState.name.matches(Regex("[A-Za-zА-ЯА-я0-9][A-Za-zА-ЯА-я0-9 ]*[A-Za-zА-ЯА-я0-9]*")) -> ValidationInfo(
                false,
                R.string.illegal_data_format
            )

            else -> ValidationInfo(true)
        }

    }


    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            decorFitsSystemWindows = true
        )
    ) {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            OutlinedTextField(modifier = Modifier
                .padding(top = 8.dp, bottom = 12.dp, start = 8.dp, end = 8.dp)
                .fillMaxWidth(),
                value = ingredientState.name,
                isError = !nameError.isValid,
                label = {
                    Text(
                        text = stringResource(R.string.ingredient_name_input),
                    )
                },
                supportingText = {
                    if (!nameError.isValid)
                        nameError.errorInfoID?.let {
                            Text(
                                text = stringResource(it, *nameError.formatArgs.toTypedArray()),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.W400,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                },
                onValueChange = {
                    ingredientState = ingredientState.copy(
                        name = it
                    )
                }
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .weight(2f)
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = !amountError.isValid,
                    label = {
                        Text(
                            text = stringResource(R.string.ingredient_amount_input),
                        )
                    },
                    value = amountInput,
                    supportingText = {
                        AnimatedVisibility(!amountError.isValid) {
                            amountError.errorInfoID?.let {
                                Text(
                                    text = stringResource(
                                        it,
                                        *amountError.formatArgs.toTypedArray()
                                    ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.W400,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    },
                    onValueChange = { amountInput = it },
                )
                SwipeableSelection(modifier = Modifier
                    .padding(horizontal = 8.dp),
                    items = Measure.entries.toList(),
                    itemHeight = 24.dp,
                    visibleItems = 2,
                    state = rememberSelectionState(
                        initialValue = Measure.entries.indexOf(
                            ingredient.measureType
                        )
                    ),
                    content = { item, isSelected ->
                        Text(
                            modifier = Modifier
                                .alpha(if (isSelected) 1f else 0.4f),
                            text = stringResource(item.fulName),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    },
                    onSelectedItemChanged = {
                        ingredientState = ingredientState.copy(measureType = Measure.entries[it])
                    })
            }
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
                    enabled = amountError.isValid && nameError.isValid,
                    onClick = {
                        onSaveChanges(
                            ingredientState.copy(
                                amount = amountInput.toFloatOrNull() ?: 0f
                            )
                        )
                    }
                ) {
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


@Composable
@Preview
private fun IngredientConfigureDialogPreview() {
    var openDialog by remember {
        mutableStateOf(!false)
    }
    val ctx = LocalContext.current
    RecipeSharing_theme(darkTheme = true) {
        IngredientConfigureDialog(onDismissRequest = {
        }) {
            Toast.makeText(
                ctx,
                "${it.name}  ${it.amount}-${it.measureType.name}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
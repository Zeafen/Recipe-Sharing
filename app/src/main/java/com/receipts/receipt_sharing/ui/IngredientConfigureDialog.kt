package com.receipts.receipt_sharing.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Text
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.data.recipes.Ingredient
import com.receipts.receipt_sharing.data.recipes.Measure
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme

@Composable
fun IngredientConfigureDialog(
    ingredient : Ingredient = Ingredient("", 0, Measure.Gram),
    onDismissRequest : () -> Unit,
    onSaveChanges : (Ingredient) -> Unit) {
    var ingredientState by remember{
        mutableStateOf(ingredient)
    }
    var amountInput by rememberSaveable {
        mutableStateOf("")
    }
    Dialog(onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            decorFitsSystemWindows = true
        )) {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            OutlinedTextField(modifier = Modifier
                .padding(top = 8.dp, bottom = 12.dp, start = 8.dp, end = 8.dp)
                .fillMaxWidth(),
                value = ingredientState.name,
                label = {
                    Text(
                        text = stringResource(R.string.ingredient_name_input),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                },
                onValueChange = { ingredientState = ingredientState.copy(
                    name = it
                ) }
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(modifier = Modifier
                    .weight(2f)
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = amountInput.isEmpty() || amountInput.length > 5,
                    label = {
                        Text(
                            text = stringResource(R.string.ingredient_amount_input),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    },
                    value = amountInput,
                    onValueChange = { amountInput = it },
                )
                SwipeableSelection(modifier = Modifier
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(horizontal = 8.dp),
                    items = Measure.entries.toList(),
                    itemHeight = 24.dp,
                    visibleItems = 2,
                    content = {item, isSelected ->
                        Text(modifier = Modifier
                            .alpha(if(isSelected)1f else 0.4f),
                            text = item.name,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    },
                    onSelectedItemChanged = {
                        ingredientState = ingredientState.copy(measureType = Measure.entries[it])
                    })
            }
            Row(modifier = Modifier
                .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 12.dp)
                    .weight(1f),
                    colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary),
                    shape = RoundedCornerShape(16.dp),
                    onClick = {
                        onSaveChanges(
                            ingredientState.copy(
                                amount = amountInput.toLongOrNull() ?: 0L
                            )
                        )
                    }
                ) {
                    Text(style = MaterialTheme.typography.titleSmall,
                        text = stringResource(id = R.string.save_changes_str))
                }
                Button(modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError),
                    shape = RoundedCornerShape(16.dp),
                    onClick = onDismissRequest) {
                    Text(style = MaterialTheme.typography.titleSmall,
                        text = stringResource(id = R.string.cancel_changes_str))
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
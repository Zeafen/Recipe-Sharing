package com.receipts.receipt_sharing.ui.recipe.elements

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme

/**
 * Composes recipes filtering tab
 * @param modifier Modifier applied to the tab
 * @param onRemoveFilter called when user clicks on "remove" on filter cell
 * @param filters applied filters list
 * @param ingredients selected ingredients list
 * @param onAddIngredient called when user clicks on "Add ingredient" button
 * @param onRemoveIngredient called when user clicks on "remove" button on Ingredient cell
 */
@Composable
fun FiltersTab(
    modifier: Modifier = Modifier,
    ingredients: List<String>,
    onRemoveIngredient: (String) -> Unit,
    filters: List<String>,
    onRemoveFilter: (String) -> Unit,
    onAddIngredient: (String) -> Unit,
    minTime : Int = 0,
    maxTime : Int = 99999,
    timeFrom : Int,
    timeTo : Int,
    onTimeFromChanged : (Int) -> Unit,
    onTimeToChanged : (Int) -> Unit,
    onClearFilters : () -> Unit,
    onConfirmFilters : () -> Unit
) {
    val ctx = LocalContext.current
    var ingredientInput by rememberSaveable {
        mutableStateOf("")
    }
    val isError = remember(ingredientInput, ingredients) {
        ingredientInput.isEmpty() || ingredients.any { it == ingredientInput } || !ingredientInput.matches(
            Regex("[A-Za-zА-Яа-я0-9 ]{2,}")
        )
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(R.string.filters_lbl),
            style = MaterialTheme.typography.titleLarge,
            letterSpacing = TextUnit(
                0.15f,
                TextUnitType.Em
            ),
            textAlign = TextAlign.Start,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.W400,
        )
        LazyRow {
            if (filters.isNotEmpty())
                items(filters) { filter ->
                    Row(
                        modifier = Modifier
                            .padding(
                                vertical = 12.dp,
                                horizontal = 4.dp
                            )
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiaryContainer),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(start = 8.dp),
                            text = filter,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        IconButton(onClick = {
                            onRemoveFilter(filter)
                        }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            else {
                item {
                    Text(
                        modifier = Modifier
                            .padding(
                                vertical = 12.dp,
                                horizontal = 8.dp
                            )
                            .alpha(0.35f),
                        text = stringResource(id = R.string.no_saved_filters),
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .padding(vertical = 4.dp),
            thickness = 2.dp
        )
        HorizontalDivider(
            modifier = Modifier
                .padding(vertical = 4.dp),
            thickness = 2.dp
        )
        Text(
            text = stringResource(R.string.recipe_tab_ingredients),
            style = MaterialTheme.typography.titleLarge,
            letterSpacing = TextUnit(
                0.15f,
                TextUnitType.Em
            ),
            textAlign = TextAlign.Start,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.W400,
        )

        OutlinedTextField(
            modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
            singleLine = true,
            label = {
                Text(
                    text = stringResource(
                        R.string.ingredient_name_input
                    )
                )
            },
            trailingIcon = {
                IconButton(
                    enabled = !isError,
                    onClick = { onAddIngredient(ingredientInput) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            },
            value = ingredientInput,
            onValueChange = { ingredientInput = it },
            isError = isError,
            supportingText = {
                if (isError)
                    Text(
                        style = MaterialTheme.typography.bodyLarge,
                        text = when {
                            ingredientInput.isEmpty() -> stringResource(R.string.empty_field_error)
                            !ingredientInput.matches(Regex("[A-Za-z0-9]{2,}")) -> stringResource(R.string.illegal_data_format)
                            else -> stringResource(R.string.values_repeating_error)
                        },
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.W400,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        letterSpacing = TextUnit(
                            0.1f,
                            TextUnitType.Em
                        )
                    )

            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (!isError)
                        onAddIngredient(ingredientInput)
                    else Toast.makeText(ctx, R.string.illegal_data_format, Toast.LENGTH_SHORT)
                        .show()
                }
            )
        )
        LazyRow {
            if (ingredients.isNotEmpty())
                items(ingredients) { ingredient ->
                    Row(
                        modifier = Modifier
                            .padding(
                                vertical = 12.dp,
                                horizontal = 4.dp
                            )
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiaryContainer),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(start = 8.dp),
                            text = ingredient,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        IconButton(onClick = { onRemoveIngredient(ingredient) }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            else {
                item {
                    Text(
                        modifier = Modifier
                            .padding(
                                vertical = 12.dp,
                                horizontal = 8.dp
                            )
                            .alpha(0.35f),
                        text = stringResource(id = R.string.no_saved_filters),
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier
                .padding(vertical = 4.dp),
            thickness = 2.dp
        )
        HorizontalDivider(
            modifier = Modifier
                .padding(vertical = 4.dp),
            thickness = 2.dp
        )
        Text(
            text = stringResource(R.string.time_lbl),
            style = MaterialTheme.typography.titleLarge,
            letterSpacing = TextUnit(
                0.15f,
                TextUnitType.Em
            ),
            textAlign = TextAlign.Start,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.W400,
        )
        TimeSlider(
            minTime = minTime,
            maxTime = maxTime,
            currentTimeTo = timeTo,
            currentTimeFrom = timeFrom,
            onTimeToChanged = onTimeToChanged,
            onTimeFromChanged = onTimeFromChanged
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 12.dp)
                    .weight(1f),
                colors = buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                shape = RoundedCornerShape(16.dp),
                onClick = onConfirmFilters
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
                colors = buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                shape = RoundedCornerShape(16.dp),
                onClick = onClearFilters
            ) {
                Text(
                    style = MaterialTheme.typography.titleSmall,
                    text = stringResource(id = R.string.cancel_txt)
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    RecipeSharing_theme {
        Surface {
            FiltersTab(
                ingredients = listOf("Ingr1", "Ingr2", "Ingr3", "Ingr4"),
                filters = listOf("F1", "F2", "F3", "F4"),
                onRemoveFilter = {},
                onRemoveIngredient = {},
                onAddIngredient = {},
                maxTime = 125,
                minTime = 0,
                timeFrom = 25,
                timeTo = 75,
                onTimeToChanged = {},
                onTimeFromChanged = {},
                onConfirmFilters = {},
                onClearFilters = {}
            )
        }
    }
}
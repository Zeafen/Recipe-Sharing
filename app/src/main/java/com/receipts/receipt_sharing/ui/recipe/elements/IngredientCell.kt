package com.receipts.receipt_sharing.ui.recipe.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.receipts.receipt_sharing.domain.recipes.Ingredient
import com.receipts.receipt_sharing.domain.recipes.Measure
import java.util.Locale

/**
 * Composes ingredient cell
 * @param modifier Modifier applied to the IngredientCell
 * @param ingredient Ingredient information
 */
@Composable
fun IngredientCell(
    modifier: Modifier = Modifier,
    ingredient: Ingredient
    ) {
    Row(modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,) {
        Text(modifier = Modifier
            .padding(horizontal = 8.dp),
            text = ingredient.name,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleLarge,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            fontWeight = FontWeight.W400
        )

        Row {
            Text(modifier = Modifier
                .padding(start = 8.dp),
                text = "(${String.format(
                    Locale.getDefault(),
                    "%.2f",ingredient.amount)})",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontWeight = FontWeight.W400
            )
            Text(
                text = stringResource(ingredient.measureType.shortName),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodyLarge,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontWeight = FontWeight.W400
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    Surface {
        Column {
            repeat(100){
                IngredientCell(modifier = Modifier
                    .fillMaxWidth(),
                    ingredient = Ingredient("Some new name", 1f, measureType = Measure.Gram)
                )
            }
        }
    }
}
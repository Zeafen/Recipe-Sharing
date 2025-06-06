package com.receipts.receipt_sharing.ui.recipe.elements

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import com.receipts.receipt_sharing.domain.filters.RecipeOrdering

/**
 * Composes amount selection drop down menu
 * @param modifier Modifier applied to DropDowMenu
 * @param expanded if drop down menu is expanded
 * @param onSelectOrdering called when users clicks on ordering item
 * @param selectedOrder current selected ordering value
 * @param isAscending if selected order is ascending
 * @param onDismissRequest called when user tries to dismiss menu
 */
@Composable
fun RecipeOrderingDropDownMenu(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onSelectOrdering: (RecipeOrdering) -> Unit,
    selectedOrder: RecipeOrdering?,
    isAscending: Boolean,
    expanded: Boolean
) {
    val rotation by animateFloatAsState(if (isAscending) 0f else 180f)
    DropdownMenu(
        modifier = modifier,
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        RecipeOrdering.entries.forEach { cell ->
            DropdownMenuItem(
                text = { Text(stringResource(cell.nameRes)) },
                leadingIcon = {
                    if (cell == selectedOrder) {
                        Icon(
                            modifier = Modifier
                                .rotate(rotation),
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = null
                        )
                    }
                },
                onClick = {
                    onSelectOrdering(cell)
                })
        }
    }
}
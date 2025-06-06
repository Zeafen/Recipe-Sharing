package com.receipts.receipt_sharing.ui.recipe.elements

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.receipts.receipt_sharing.presentation.CellsAmount

/**
 * Composes amount selection drop down menu
 * @param modifier Modifier applied to DropDowMenu
 * @param expanded if drop down menu is expanded
 * @param onSelectSize called when users clicks on size item
 * @param onDismissRequest called when user tries to dismiss menu
 */
@Composable
fun ColumnAmountDropDownMenu(modifier: Modifier = Modifier,
                             onDismissRequest : () -> Unit,
                             onSelectSize : (CellsAmount) -> Unit,
                             expanded : Boolean) {
    DropdownMenu(modifier = modifier,
        expanded = expanded,
        onDismissRequest = onDismissRequest) {
        CellsAmount.entries.forEach { cell ->
            DropdownMenuItem(text = {
                Text(stringResource(cell.nameRes))
            },
                onClick = {
                    onSelectSize(cell)
                    onDismissRequest()
                })
        }
    }
}
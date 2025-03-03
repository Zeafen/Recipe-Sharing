package com.receipts.receipt_sharing.ui.reviews

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.receipts.receipt_sharing.presentation.reviews.ReviewsSorting

@Composable
fun ReviewSortingDropDownMenu(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onSelectSorting: (ReviewsSorting) -> Unit,
    expanded: Boolean
) {
    DropdownMenu(
        modifier = modifier,
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        ReviewsSorting.entries.forEach { cell ->
            DropdownMenuItem(
                text = { Text(stringResource(cell.nameRes)) },
                onClick = {
                    onSelectSorting(cell)
                    onDismissRequest()
                })
        }
    }
}
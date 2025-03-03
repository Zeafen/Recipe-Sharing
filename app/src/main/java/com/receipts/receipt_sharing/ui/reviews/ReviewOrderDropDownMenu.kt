package com.receipts.receipt_sharing.ui.reviews

import androidx.compose.animation.core.Animatable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import com.receipts.receipt_sharing.presentation.reviews.ReviewsOrdering
import kotlinx.coroutines.launch


@Composable
fun ReviewOrderDropDownMenu(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onSelectSorting: (ReviewsOrdering) -> Unit,
    selectedOrder: ReviewsOrdering,
    isAscending: Boolean,
    expanded: Boolean
) {
    val rotation = remember {
        Animatable(0f)
    }
    val scope = rememberCoroutineScope()

    LaunchedEffect(isAscending) {
        scope.launch {
            rotation.animateTo(
                if(isAscending)
                180f
                else 0f
            )
        }
    }
    DropdownMenu(
        modifier = modifier,
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        ReviewsOrdering.entries.forEach { cell ->
            DropdownMenuItem(
                text = { Text(stringResource(cell.nameRes)) },
                leadingIcon = {
                    if (cell == selectedOrder) {
                            Icon(modifier = Modifier
                                .rotate(rotation.value),
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = null
                            )
                    }
                },
                onClick = {
                    onSelectSorting(cell)
                })
        }
    }
}
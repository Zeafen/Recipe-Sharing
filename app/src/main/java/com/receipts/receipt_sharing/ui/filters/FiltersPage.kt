package com.receipts.receipt_sharing.ui.filters

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.ExperimentalAnimationSpecApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.ui.effects.SelectionItem
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme


@Preview
@Composable
private fun Preview() {
    var namesList by remember {
        mutableStateOf(
            mapOf(
                "Country" to listOf(
                    "Japan", "China", "Russia",
                ),
                "Meal Time" to listOf(
                    "Breakfast", "Lunch", "Dinner"
                )
            )
        )
    }
    RecipeSharing_theme(darkTheme = true) {
        Surface {
            var open by remember {
                mutableStateOf(false)
            }
            var txt by remember {
                mutableStateOf("")
            }
            if (open)
                androidx.wear.compose.material.dialog.Dialog(
                    showDialog = open,
                    onDismissRequest = { open = false }) {
                    Text(text = txt)
                }
            FiltersPage(
                categorizedItems = namesList,
                onFiltersConfirmed = {
                    txt = it.toString()
                    open = true
                },
                onCancelChanges = {}
            )
        }
    }
}


data class SelectionCategory(
    var name: String,
    val items: List<SelectionItem<String>>
)

/**
 * Composes Category name header
 * @param text Category name
 * @param modifier Modifier applied to CategoryHeader
 */
@Composable
private fun CategoryHeader(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineLarge,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    )
}

/**
 * Composes filter value cell
 * @param modifier Modifier applied to SelectionCategoryItem
 * @param text filter value
 * @param onUnSelect called when user clicks on selected filter item
 * @param onSelect called when user clicks on unselected filter item
 * @param isSelected if item is selected
 */
@OptIn(ExperimentalAnimationSpecApi::class)
@Composable
private fun SelectionCategoryItem(
    isSelected: Boolean,
    text: String,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit,
    onUnSelect: () -> Unit
) {
    val animatedContainerColor = animateColorAsState(
        if(isSelected) MaterialTheme.colorScheme.onSurface
        else MaterialTheme.colorScheme.surface,
        animationSpec = tween(500, easing = EaseOutQuad)
    )
    val animatedContentColor = animateColorAsState(
        if(isSelected) MaterialTheme.colorScheme.surface
        else MaterialTheme.colorScheme.onSurface,
        animationSpec = tween(500, easing = EaseOutQuad)
    )
    TextButton(
        modifier = modifier,
        colors = ButtonDefaults.textButtonColors(
            contentColor = animatedContentColor.value,
            containerColor = animatedContainerColor.value,
        ),
        onClick = if (isSelected) onUnSelect else onSelect,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
    }

}

/**
 * Composes filters selection page
 *  @param modifier Modifier applied to FiltersPage
 *  @param onFiltersConfirmed called when user clicks on "Confirm" button
 *  @param categorizedItems filters grouped by categories
 *  @param onCancelChanges called when user clicks on "Cancel" button
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FiltersPage(
    categorizedItems: Map<String, List<String>>,
    modifier: Modifier = Modifier,
    onFiltersConfirmed: (filters: List<String>) -> Unit,
    onCancelChanges: () -> Unit
) {
    var categories = rememberSaveable(categorizedItems) {
        categorizedItems.map {
            SelectionCategory(
                name = it.key,
                items = it.value.map { value ->
                    SelectionItem(value)
                }
            )
        }
    }

    Scaffold(modifier = modifier,
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ) {
            TextButton(
                modifier = Modifier
                    .weight(1f),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
                onClick = onCancelChanges
            )
            {
                Text(
                    text = stringResource(id = R.string.cancel_txt),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            TextButton(modifier = Modifier
                .weight(1f),
                onClick = {
                    val items = mutableListOf<String>()
                    categories.forEach { sc ->
                        sc.items.forEach {
                            if (it.isSelected)
                                items.add(it.item)
                        }
                    }
                    onFiltersConfirmed(items.toList())
                }) {
                Text(
                    text = stringResource(id = R.string.confirm_txt),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        }
    ) {
        LazyColumn(
            contentPadding = it
        ) {
            categories.indices.forEach { categoryIndex ->
                stickyHeader {
                    CategoryHeader(categories[categoryIndex].name)
                }
                item {
                    LazyHorizontalGrid(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        rows = GridCells.Fixed(3)
                    ) {
                        items(categories[categoryIndex].items) {
                            SelectionCategoryItem(modifier = Modifier
                                .padding(vertical = 16.dp, horizontal = 8.dp),
                                text = it.item,
                                isSelected = it.isSelected,
                                onSelect = {
                                    categories = categories
                                        .mapIndexed { categoryInd, selectionCategory ->
                                            if (categoryInd == categoryIndex)
                                                selectionCategory
                                                    .copy(
                                                        items = selectionCategory.items.apply {
                                                            this.forEach { it.isSelected = false }
                                                            this[categories[categoryIndex].items.indexOf(
                                                                it
                                                            )].isSelected = true
                                                        }
                                                    )
                                            else selectionCategory
                                        }
                                },
                                onUnSelect = {
                                    categories =
                                        categories.mapIndexed { categoryInd, selectionCategory ->
                                            if (categoryIndex == categoryInd)
                                                selectionCategory.copy(
                                                    items = selectionCategory.items.apply {
                                                        this.forEach {
                                                            it.isSelected = false
                                                        }
                                                    }
                                                )
                                            else selectionCategory
                                        }
                                })
                        }
                    }
                }
            }
        }
    }
}
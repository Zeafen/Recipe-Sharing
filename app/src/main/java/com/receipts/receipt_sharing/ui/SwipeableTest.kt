package com.receipts.receipt_sharing.ui

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.receipts.receipt_sharing.domain.recipes.Measure
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

open class SelectionLineStyle(
    val brush: Brush,
    @FloatRange(from = 0.0, to = 1.0)
    val lengthFraction : Float,
    val strokeWidth : Float
){
    object Default : SelectionLineStyle(
        brush = Brush.linearGradient(
            listOf(Color.DarkGray, Color.LightGray)
        ),
        lengthFraction = 1f,
        strokeWidth = 4f
    )
}


@Composable
fun CustomLayout(modifier: Modifier = Modifier,
                 content : @Composable () -> Unit) {
    Layout(modifier = modifier,
        content = content,
        measurePolicy = { measurables, constraints ->
            val placeables = measurables.map {
                it.measure(constraints)
            }
            val needHeight = 0 + placeables.sumOf { p -> p.height }
            layout(placeables.maxOf { p -> p.width }, needHeight) {
                var yPosition = 0
                placeables.forEach { placeable ->
                    placeable.placeRelative(x = 0, y = yPosition)
                    yPosition += placeable.height
                }
            }
        })
}

 class SwipeableSelectionItem<E>(var item : E, isSelected : Boolean = false){
    var isSelected by mutableStateOf(isSelected)
}

class SwipeableSelectionState(lastSelectedIndex : Int = 0){
    var lastSelectedIndex by mutableStateOf(lastSelectedIndex)

    companion object {
        val Saver = object : Saver<SwipeableSelectionState, Int>{
            override fun restore(value: Int): SwipeableSelectionState {
                return SwipeableSelectionState(lastSelectedIndex = value)
            }

            override fun SaverScope.save(value: SwipeableSelectionState): Int {
                return value.lastSelectedIndex
            }
        }
    }
}


@Composable
fun rememberSwipeableSelectionState(initialValue : Int = 0) = rememberSaveable(saver = SwipeableSelectionState.Saver){
    SwipeableSelectionState(initialValue)
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun <T> SwipeableSelection(
    modifier : Modifier = Modifier,
    selectionLineStyle: SelectionLineStyle = SelectionLineStyle.Default,
    state : SwipeableSelectionState = rememberSwipeableSelectionState(),
    items : List<T>,
    content: @Composable RowScope.(T, Boolean) -> Unit,
    itemHeight : Dp,
    visibleItems : Int = 3,
    onSelectedItemChanged : (selectedIndex : Int) -> Unit
) {

    val selectionItems = remember(items) {
        items.map {
            SwipeableSelectionItem(it)
        }
    }
    val swipeableState = rememberSwipeableState(initialValue = state.lastSelectedIndex) {
        selectionItems[state.lastSelectedIndex].isSelected = false
        selectionItems[it].isSelected = true
        onSelectedItemChanged(it)
        state.lastSelectedIndex = it
        true
    }
    val sizePx = with(LocalDensity.current) { itemHeight.toPx() }
    val anchors = remember(items) {
        val anchors = mutableMapOf<Float, Int>()
        for (index in items.indices) {
            anchors[-index * sizePx] = index
        }
        anchors
    }


    Box(
        modifier = Modifier
            .height(itemHeight*visibleItems),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .height(itemHeight)
                .swipeable(
                    state = swipeableState,
                    anchors = anchors,
                    thresholds = { _, _ -> FractionalThreshold(0.3f) },
                    orientation = Orientation.Vertical
                )
                .background(Color.White)
                .drawWithContent {
                    val width = drawContext.size.width
                    val startFraction = (1 - selectionLineStyle.lengthFraction) / 2f
                    val endFraction = startFraction + selectionLineStyle.lengthFraction
                    drawContent()
                    drawLine(
                        brush = selectionLineStyle.brush,
                        start = Offset(
                            width * startFraction,
                            sizePx
                        ),
                        end = Offset(
                            width * endFraction,
                            sizePx
                        ),
                        strokeWidth = 3f
                    )
                    drawLine(
                        brush = selectionLineStyle.brush,
                        start = Offset(
                            width * startFraction,
                            0f
                        ),
                        end = Offset(
                            width * endFraction,
                            0f
                        ),
                        strokeWidth = selectionLineStyle.strokeWidth
                    )
                }
        ) {
            CustomLayout(modifier = Modifier
                .then(modifier)
                .offset { IntOffset(0, swipeableState.offset.value.toInt()) }
                .height(itemHeight * visibleItems)
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)

                    val placeableYMultiplier = (items.size - 1).toFloat() / 2f
                    layout(placeable.width, placeable.height) {
                        placeable.placeRelative(
                            IntOffset(
                                0,
                                (placeableYMultiplier * sizePx).roundToInt()
                            )
                        )
                    }
                }) {
                selectionItems.forEach {
                    Row(
                        modifier = Modifier
                            .height(itemHeight),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if(it.isSelected || (!it.isSelected && visibleItems-1 >= (state.lastSelectedIndex-selectionItems.indexOf(it)).absoluteValue))
                            content(it.item, it.isSelected)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    RecipeSharing_theme {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SwipeableSelection<Measure>(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer),
                items = Measure.entries.toList(),
                itemHeight = 30.dp,
                content = { item, isSelected ->
                    Text(text = item.name, color = if (isSelected) Color.Black else Color.Gray)
                },
                visibleItems = 2,
                onSelectedItemChanged = {}
            )
        }
    }
}

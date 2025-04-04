package com.receipts.receipt_sharing.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Composes scalable two-layer app bar
 * @param modifier Modifier applied to TwoLayerTopAppBar
 * @param title title content
 * @param additionalContent Under title content
 * @param actions action icons content
 * @param navigationIcon navigation icon content
 * @param colors top app bar colors
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwoLayerTopAppBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    additionalContent: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    navigationIcon: (@Composable () -> Unit)? = null,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    windowInsets : WindowInsets = TopAppBarDefaults.windowInsets
) {
    Surface {
        Layout(
            content = {
                if (navigationIcon != null)
                    Box(
                        Modifier
                            .wrapContentSize()
                            .layoutId("navigationIcon")
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor provides colors.navigationIconContentColor,
                            content = navigationIcon
                        )
                    }

                Box(
                    Modifier
                        .wrapContentSize()
                        .layoutId("title")
                ) {
                    CompositionLocalProvider(
                        LocalContentColor provides colors.titleContentColor,
                        LocalTextStyle provides MaterialTheme.typography.headlineSmall,
                        content = title
                    )
                }

                if (actions != null)
                    Box(
                        Modifier
                            .wrapContentSize()
                            .layoutId("actions")
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor provides colors.actionIconContentColor,
                            content = @Composable {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.End,
                                    content = actions
                                )
                            }
                        )
                    }
                if (additionalContent != null)
                    Box(
                        Modifier
                            .wrapContentSize()
                            .layoutId("additionalContent")
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor provides colors.titleContentColor,
                            LocalTextStyle provides MaterialTheme.typography.bodyLarge,
                            content = additionalContent
                        )
                    }


            },
            modifier = modifier
                .windowInsetsPadding(windowInsets)
                // clip after padding so we don't show the title over the inset area
                .clipToBounds()
                .background(colors.containerColor)
        ) { measurables, constraints ->
            val navigationIconPlaceable =
                measurables.firstOrNull { it.layoutId == "navigationIcon" }
                    ?.measure(constraints.copy(minWidth = 0))

            val actionsPlaceable = measurables.firstOrNull { it.layoutId == "actions" }
                ?.measure(constraints.copy(minWidth = 0))

            val navigationIconsOffset = navigationIconPlaceable?.let {
                navigationIconPlaceable.width + HORIZONTAL_OFFSET.toPx()
            } ?: 16.dp.toPx()
            val actionsOffset = actionsPlaceable?.let {
                actionsPlaceable.width + HORIZONTAL_OFFSET.toPx()
            } ?: 16.dp.toPx()

            val titlePlaceable = measurables.firstOrNull { it.layoutId == "title" }
                ?.measure(constraints.copy(maxWidth = (constraints.maxWidth - navigationIconsOffset - actionsOffset).roundToInt()))

            val additionalContentPlaceable =
                measurables.firstOrNull { it.layoutId == "additionalContent" }
                    ?.measure(constraints.copy(maxWidth = (constraints.maxWidth - HORIZONTAL_OFFSET.toPx()*2).roundToInt()))

            val titleLineHeight = max(
                40.dp.toPx(),
                titlePlaceable?.height?.toFloat() ?: 40.dp.toPx()
            )

            val navigationIconY = ((titleLineHeight - (navigationIconPlaceable?.height ?: 0)) / 2).roundToInt() + HORIZONTAL_OFFSET.toPx().roundToInt()
            val navigationIconX = HORIZONTAL_OFFSET.toPx().roundToInt()

            val actionsY = ((titleLineHeight - (actionsPlaceable?.height ?: 0)) / 2).roundToInt() + HORIZONTAL_OFFSET.toPx().roundToInt()
            val actionsX = (constraints.maxWidth - (actionsPlaceable?.width?:0) - (HORIZONTAL_OFFSET.toPx()/2)).roundToInt()

            val titleX = (navigationIconPlaceable?.width ?: 0) + HORIZONTAL_OFFSET.toPx().roundToInt()
            val titleY = ((titleLineHeight - (titlePlaceable?.height ?: 0)) / 2).roundToInt() + HORIZONTAL_OFFSET.toPx().roundToInt()

            val additionalContentY = titleY + titleLineHeight.roundToInt() + VERTICAL_OFFSET.toPx().roundToInt()
            val additionalContentX = HORIZONTAL_OFFSET.toPx().roundToInt()

            val appBarHeight = additionalContentY + (additionalContentPlaceable?.height?:0)
            layout(constraints.maxWidth, appBarHeight){
                navigationIconPlaceable?.placeRelative(navigationIconX, navigationIconY)
                titlePlaceable?.placeRelative(titleX, titleY)
                actionsPlaceable?.placeRelative(actionsX, actionsY)
                additionalContentPlaceable?.placeRelative(additionalContentX, additionalContentY)
            }
        }
    }
}

private val HORIZONTAL_OFFSET = 8.dp
private val VERTICAL_OFFSET = 12.dp
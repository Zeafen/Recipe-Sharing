package com.receipts.receipt_sharing.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun SectionSelectionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: androidx.compose.ui.graphics.Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    trailingIcon: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    title: @Composable () -> Unit,
    supportingText: (@Composable () -> Unit)? = null
) {
    Surface(
        onClick = onClick,
        modifier = modifier.semantics { role = Role.Button },
        enabled = enabled,
        shape = shape,
        color = if (enabled) colors.containerColor else colors.disabledContainerColor,
        border = border,
    ) {
        Layout(
            modifier = Modifier
                .padding(contentPadding),
            content = {
                if (leadingIcon != null)
                    Box(
                        Modifier
                            .wrapContentSize()
                            .layoutId("leadingIcon")
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor provides if (enabled) colors.contentColor else colors.disabledContentColor,
                            content = leadingIcon
                        )
                    }
                Box(
                    Modifier
                        .wrapContentSize()
                        .layoutId("title")
                ) {
                    CompositionLocalProvider(
                        LocalContentColor provides if (enabled) colors.contentColor else colors.disabledContentColor,
                        LocalTextStyle provides MaterialTheme.typography.titleLarge,
                        content = title
                    )
                }
                if (trailingIcon != null)
                    Box(
                        Modifier
                            .wrapContentSize()
                            .layoutId("trailingIcon")
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor provides if (enabled) colors.contentColor else colors.disabledContentColor,
                            content = trailingIcon
                        )
                    }

                if (supportingText != null)
                    Box(
                        Modifier
                            .wrapContentSize()
                            .layoutId("supportingText")
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor provides if (enabled) colors.contentColor else colors.disabledContentColor,
                            LocalTextStyle provides MaterialTheme.typography.bodyLarge,
                            content = supportingText
                        )
                    }
            }
        ) { measurables, constraints ->
            val leadingPlaceable = measurables.firstOrNull { it.layoutId == "leadingIcon" }
                ?.measure(constraints.copy(minWidth = 0))
            val trailingPlaceable = measurables.firstOrNull { it.layoutId == "trailingIcon" }
                ?.measure(constraints.copy(minWidth = 0))

            val leadingOffset = leadingPlaceable?.let {
                it.width + PADDING.toPx()
            } ?: 12.dp.toPx()
            val trailingOffset = trailingPlaceable?.let {
                it.width + PADDING.toPx()
            } ?: 12.dp.toPx()

            val titlePlaceable = measurables.firstOrNull { it.layoutId == "title" }
                ?.measure(constraints.copy(maxWidth = (constraints.maxWidth - leadingOffset - trailingOffset).roundToInt()))
            val titleLineHeight = max(
                40.dp.toPx(),
                titlePlaceable?.height?.toFloat() ?: 0.dp.toPx()
            )

            val supportingPlaceable = measurables.firstOrNull { it.layoutId == "supportingText" }
                ?.measure(constraints.copy(minWidth = 0, maxWidth = constraints.maxWidth - PADDING.toPx().roundToInt()))

            val leadingY = ((titleLineHeight - (leadingPlaceable?.height
                ?: 0)) / 2).roundToInt() + (PADDING.toPx() / 2).roundToInt()
            val leadingX = PADDING.toPx().roundToInt()

            val titleY = ((titleLineHeight - (titlePlaceable?.height
                ?: 0)) / 2).roundToInt() + (PADDING.toPx() / 2).roundToInt()
            val titleX =
                leadingX + (leadingPlaceable?.width ?: 0) + PADDING.toPx().roundToInt()

            val supportingTextY = titleLineHeight.roundToInt() + PADDING.toPx().roundToInt()
            val supportingTextX = PADDING.toPx().roundToInt()

            val trailingY = ((titleLineHeight - (trailingPlaceable?.height
                ?: 0)) / 2).roundToInt() + (PADDING.toPx() / 2).roundToInt()
            val trailingX = constraints.maxWidth - (trailingPlaceable?.width ?: 0)

            val sectionHeight = supportingPlaceable?.let {
                supportingTextY + it.height + (PADDING.toPx()/2).roundToInt()
            } ?: (titleLineHeight.roundToInt() + (PADDING.toPx()/2).roundToInt())

            layout(constraints.maxWidth, sectionHeight) {
                leadingPlaceable?.placeRelative(leadingX, leadingY)
                titlePlaceable?.placeRelative(titleX, titleY)
                trailingPlaceable?.placeRelative(trailingX, trailingY)
                supportingPlaceable?.placeRelative(supportingTextX, supportingTextY)
            }
        }
    }
}

private val PADDING = 8.dp

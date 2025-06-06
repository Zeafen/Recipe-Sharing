package com.receipts.receipt_sharing.ui.recipe.elements

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.receipts.receipt_sharing.domain.recipes.RecipeDifficulty
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme

@Composable
fun DifficultyMeter(
    modifier: Modifier = Modifier,
    difficulty: RecipeDifficulty
) {
    val arcDegrees = 275
    val startArcAngle = 135f
    val startStepAngle = -45
    val numberOfMarkers = RecipeDifficulty.entries.size
    val degreesMarkerStep = arcDegrees / numberOfMarkers
    val progress = RecipeDifficulty.entries.indexOf(difficulty) + 1

    Canvas(
        modifier = modifier
            .aspectRatio(1f),
        onDraw = {
            drawIntoCanvas { canvas ->
                val w = drawContext.size.width
                val h = drawContext.size.height
                val centerOffset = Offset(w / 2f, h / 2f)

                val (progressColor, progressBackgroundColor) = when (difficulty) {
                    RecipeDifficulty.Beginner -> Color(0xFF388E3C) to Color(0xFFC8E6C9)
                    RecipeDifficulty.Common -> Color(0xFFCCC916) to Color(0xFFE5E6C8)
                    RecipeDifficulty.Adept -> Color(0xFFE3700B) to Color(0xFFE5C5A3)
                    RecipeDifficulty.MasterPiece -> Color(0xFFFF0000) to Color(0xFFE6C8C8)
                }
                val paint = Paint().apply {
                    color = progressColor
                }
                val centerArcSize = Size(w, h)
                val centerArcStroke = Stroke(w/5f, 0f, StrokeCap.Round)
                drawArc(
                    progressBackgroundColor,
                    startArcAngle,
                    arcDegrees.toFloat(),
                    false,
                    size = centerArcSize,
                    style = centerArcStroke
                )
                // Drawing Center Arc progress
                drawArc(
                    progressColor,
                    startArcAngle,
                    (degreesMarkerStep * progress).toFloat(),
                    false,
                    size = centerArcSize,
                    style = centerArcStroke
                )
                // Drawing the pointer circle
                drawCircle(progressColor, w/6f, centerOffset)
                drawCircle(Color.White, w/9f, centerOffset)
                drawCircle(Color.Black, w/12f, centerOffset)

                val degrees = startStepAngle + degreesMarkerStep * progress
                canvas.save()
                canvas.rotate(degrees.toFloat(), w / 2f, h / 2f)
                // Drawing Pointer
                paint.color = Color.Black
                canvas.drawPath(
                    Path().apply {
                        moveTo(w / 2f, (h / 2) - 15f)
                        lineTo(w / 2f, (h / 2) + 15f)
                        lineTo(w / 10f, h / 2f)
                        lineTo(w / 10f, (h / 2f) - 10f)
                        lineTo(w / 2f, (h / 2f) - 15f)
                        close()
                    },
                    paint
                )

                canvas.restore()
            }
        })
}


@Preview
@Composable
private fun DifficultyMeterPreview() {
    RecipeSharing_theme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                DifficultyMeter(modifier = Modifier
                    .size(128.dp),
                    difficulty = RecipeDifficulty.Adept
                )
            }
        }
    }
}
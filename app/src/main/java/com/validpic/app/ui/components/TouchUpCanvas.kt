package com.validpic.app.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Path as AndroidPath
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.validpic.app.ui.theme.SlateBorder

enum class TouchUpTool {
    ERASE_BACKGROUND,
    RESTORE_SUBJECT
}

data class PaintStroke(
    val points: List<Offset>,
    val tool: TouchUpTool,
    val brushRadius: Float
)

/**
 * Interactive touch-up canvas allowing users to fine-tune hair and boundary edges.
 */
@Composable
fun TouchUpCanvas(
    baseBitmap: Bitmap,
    currentTool: TouchUpTool,
    brushRadius: Float,
    strokeList: List<PaintStroke>,
    onStrokeFinished: (PaintStroke) -> Unit,
    aspectRatio: Float,
    modifier: Modifier = Modifier
) {
    val currentPoints = remember { mutableStateListOf<Offset>() }

    Box(
        modifier = modifier
            .fillMaxWidth(0.85f)
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
            .background(Color.White)
            .pointerInput(currentTool, brushRadius) {
                detectDragGestures(
                    onDragStart = { offset ->
                        currentPoints.clear()
                        currentPoints.add(offset)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        currentPoints.add(change.position)
                    },
                    onDragEnd = {
                        if (currentPoints.isNotEmpty()) {
                            onStrokeFinished(PaintStroke(currentPoints.toList(), currentTool, brushRadius))
                            currentPoints.clear()
                        }
                    },
                    onDragCancel = {
                        currentPoints.clear()
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            // Draw underlying base image
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawBitmap(
                    baseBitmap,
                    null,
                    android.graphics.Rect(0, 0, size.width.toInt(), size.height.toInt()),
                    null
                )
            }

            // Draw all confirmed strokes
            for (stroke in strokeList) {
                if (stroke.points.size > 1) {
                    val paintColor = if (stroke.tool == TouchUpTool.ERASE_BACKGROUND) {
                        Color.White
                    } else {
                        Color(0xFF0066CC).copy(alpha = 0.35f) // Visual indicator for restored area
                    }

                    for (i in 0 until stroke.points.size - 1) {
                        drawLine(
                            color = paintColor,
                            start = stroke.points[i],
                            end = stroke.points[i + 1],
                            strokeWidth = stroke.brushRadius * 2f
                        )
                    }
                }
            }

            // Draw current active stroke
            if (currentPoints.size > 1) {
                val activeColor = if (currentTool == TouchUpTool.ERASE_BACKGROUND) {
                    Color.White
                } else {
                    Color(0xFF0066CC).copy(alpha = 0.35f)
                }

                for (i in 0 until currentPoints.size - 1) {
                    drawLine(
                        color = activeColor,
                        start = currentPoints[i],
                        end = currentPoints[i + 1],
                        strokeWidth = brushRadius * 2f
                    )
                }
            }
        }
    }
}

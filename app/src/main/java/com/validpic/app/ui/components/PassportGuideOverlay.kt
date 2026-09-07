package com.validpic.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.validpic.app.R

/**
 * Visual passport alignment guide overlay for CameraX.
 * Renders a darkened semi-transparent backdrop with a clear passport oval cutout
 * and official framing reference marks (Head top, Eye level, Chin).
 */
@Composable
fun PassportGuideOverlay(
    modifier: Modifier = Modifier,
    guidanceText: String = stringResource(R.string.camera_guidance)
) {
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // Necessary for BlendMode.Clear to cut through the dark overlay
                    alpha = 0.99f
                }
        ) {
            val canvasW = size.width
            val canvasH = size.height

            // Calculate oval dimensions: roughly 65% of screen width, 4:5 aspect ratio
            val ovalWidth = canvasW * 0.65f
            val ovalHeight = ovalWidth * 1.35f
            val ovalLeft = (canvasW - ovalWidth) / 2f
            val ovalTop = canvasH * 0.22f

            // 1. Draw semi-transparent dark mask over entire screen
            drawRect(
                color = Color.Black.copy(alpha = 0.65f),
                size = size
            )

            // 2. Cut out clear oval in center for user face alignment
            val ovalPath = Path().apply {
                addOval(
                    Rect(
                        left = ovalLeft,
                        top = ovalTop,
                        right = ovalLeft + ovalWidth,
                        bottom = ovalTop + ovalHeight
                    )
                )
            }
            drawPath(
                path = ovalPath,
                color = Color.Transparent,
                blendMode = BlendMode.Clear
            )

            // 3. Draw bright guide boundary around oval
            drawOval(
                color = Color.White.copy(alpha = 0.85f),
                topLeft = Offset(ovalLeft, ovalTop),
                size = Size(ovalWidth, ovalHeight),
                style = Stroke(width = 3.dp.toPx())
            )

            // 4. Subtle dashed horizontal guideline for eye-level (around 45% down the oval)
            val eyeLevelY = ovalTop + (ovalHeight * 0.45f)
            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)

            drawLine(
                color = Color.White.copy(alpha = 0.5f),
                start = Offset(ovalLeft + (ovalWidth * 0.15f), eyeLevelY),
                end = Offset(ovalLeft + (ovalWidth * 0.85f), eyeLevelY),
                strokeWidth = 2.dp.toPx(),
                pathEffect = dashEffect
            )
        }

        // Floating guidance badge at top center
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 40.dp)
                .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = guidanceText,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

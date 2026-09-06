package com.formpic.app.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formpic.app.R
import com.formpic.app.ui.theme.NavyDeep
import com.formpic.app.ui.theme.SlateBorder

/**
 * Interactive Before & After comparison widget.
 * Allows users to inspect original vs white-background processed photo.
 */
@Composable
fun BeforeAfterSlider(
    originalBitmap: Bitmap,
    processedBitmap: Bitmap,
    aspectRatio: Float,
    modifier: Modifier = Modifier
) {
    var showProcessed by remember { mutableStateOf(true) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Comparison Toggle Tabs
        Row(
            modifier = Modifier
                .background(Color(0xFFF1F5F9), RoundedCornerShape(24.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (!showProcessed) NavyDeep else Color.Transparent)
                    .clickable { showProcessed = false }
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = stringResource(R.string.view_before),
                    color = if (!showProcessed) Color.White else Color(0xFF64748B),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (showProcessed) NavyDeep else Color.Transparent)
                    .clickable { showProcessed = true }
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = stringResource(R.string.view_after),
                    color = if (showProcessed) Color.White else Color(0xFF64748B),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Image Display with Frame Border
        Box(
            modifier = Modifier
                .padding(top = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                .background(Color.White)
        ) {
            Crossfade(
                targetState = showProcessed,
                animationSpec = tween(durationMillis = 200),
                label = "BeforeAfterCrossfade"
            ) { targetProcessed ->
                val bitmapToDisplay = if (targetProcessed) processedBitmap else originalBitmap
                Image(
                    bitmap = bitmapToDisplay.asImageBitmap(),
                    contentDescription = if (targetProcessed) "Processed Photo" else "Original Photo",
                    modifier = Modifier
                        .fillMaxWidth(0.70f)
                        .aspectRatio(aspectRatio),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

package com.validpic.app.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.validpic.app.R
import com.validpic.app.ui.components.ValidPicTopAppBar
import com.validpic.app.ui.components.PaintStroke
import com.validpic.app.ui.components.TouchUpCanvas
import com.validpic.app.ui.components.TouchUpTool
import com.validpic.app.ui.theme.BlueAccent
import com.validpic.app.ui.theme.NavyDeep
import com.validpic.app.ui.theme.SlateBorder
import com.validpic.app.ui.theme.SlateTextPrimary
import com.validpic.app.ui.theme.SlateTextSecondary

@Composable
fun TouchUpScreen(
    baseBitmap: Bitmap,
    aspectRatio: Float,
    onApplyTouchUp: (List<PaintStroke>) -> Unit,
    onNavigateBack: () -> Unit
) {
    var currentTool by remember { mutableStateOf(TouchUpTool.ERASE_BACKGROUND) }
    var brushRadius by remember { mutableFloatStateOf(20f) }

    val strokeHistory = remember { mutableStateListOf<PaintStroke>() }
    val undoneStrokes = remember { mutableStateListOf<PaintStroke>() }

    Scaffold(
        topBar = {
            ValidPicTopAppBar(
                title = stringResource(R.string.touchup_title),
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(1.dp, SlateBorder)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { onApplyTouchUp(strokeHistory.toList()) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyDeep)
                ) {
                    Text(
                        text = stringResource(R.string.apply_touchup),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.touchup_instruction),
                fontSize = 13.sp,
                color = SlateTextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Canvas Area
            TouchUpCanvas(
                baseBitmap = baseBitmap,
                currentTool = currentTool,
                brushRadius = brushRadius,
                strokeList = strokeHistory,
                onStrokeFinished = { stroke ->
                    strokeHistory.add(stroke)
                    undoneStrokes.clear()
                },
                aspectRatio = aspectRatio
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tool selection buttons: Erase vs Restore
            Row(
                modifier = Modifier.fillMaxWidth(0.85f),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { currentTool = TouchUpTool.ERASE_BACKGROUND },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentTool == TouchUpTool.ERASE_BACKGROUND) NavyDeep else Color(0xFFE2E8F0),
                        contentColor = if (currentTool == TouchUpTool.ERASE_BACKGROUND) Color.White else SlateTextPrimary
                    )
                ) {
                    Icon(imageVector = Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Erase", fontSize = 13.sp)
                }

                Button(
                    onClick = { currentTool = TouchUpTool.RESTORE_SUBJECT },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentTool == TouchUpTool.RESTORE_SUBJECT) NavyDeep else Color(0xFFE2E8F0),
                        contentColor = if (currentTool == TouchUpTool.RESTORE_SUBJECT) Color.White else SlateTextPrimary
                    )
                ) {
                    Icon(imageVector = Icons.Default.Brush, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Restore", fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Brush size slider
            Row(
                modifier = Modifier.fillMaxWidth(0.85f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Brush Size", fontSize = 12.sp, color = SlateTextSecondary)
                Spacer(modifier = Modifier.width(12.dp))
                Slider(
                    value = brushRadius,
                    onValueChange = { brushRadius = it },
                    valueRange = 8f..50f,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(thumbColor = BlueAccent, activeTrackColor = BlueAccent)
                )
            }

            // Undo / Redo controls
            Row(
                modifier = Modifier.fillMaxWidth(0.85f),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = {
                        if (strokeHistory.isNotEmpty()) {
                            undoneStrokes.add(strokeHistory.removeLast())
                        }
                    },
                    enabled = strokeHistory.isNotEmpty()
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Undo, contentDescription = stringResource(R.string.undo))
                }

                IconButton(
                    onClick = {
                        if (undoneStrokes.isNotEmpty()) {
                            strokeHistory.add(undoneStrokes.removeLast())
                        }
                    },
                    enabled = undoneStrokes.isNotEmpty()
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Redo, contentDescription = stringResource(R.string.redo))
                }
            }
        }
    }
}

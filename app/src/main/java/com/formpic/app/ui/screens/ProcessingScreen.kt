package com.formpic.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formpic.app.R
import com.formpic.app.data.model.ProcessingPhase
import com.formpic.app.ui.theme.BlueAccent
import com.formpic.app.ui.theme.EmeraldSuccess
import com.formpic.app.ui.theme.NavyDeep
import com.formpic.app.ui.theme.SlateTextPrimary
import com.formpic.app.ui.theme.SlateTextSecondary

@Composable
fun ProcessingScreen(
    phase: ProcessingPhase,
    modifier: Modifier = Modifier
) {
    val stepIndex = when (phase) {
        is ProcessingPhase.Loading -> 1
        is ProcessingPhase.DetectingFace -> 2
        is ProcessingPhase.RemovingBackground -> 3
        is ProcessingPhase.Compressing -> 4
        is ProcessingPhase.Success -> 5
        else -> 0
    }

    val currentMessage = when (phase) {
        is ProcessingPhase.Loading -> phase.message
        is ProcessingPhase.DetectingFace -> phase.message
        is ProcessingPhase.RemovingBackground -> phase.message
        is ProcessingPhase.Compressing -> phase.message
        else -> "Preparing photo…"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Circular Spinner with brand styling
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(80.dp),
                    color = BlueAccent,
                    strokeWidth = 6.dp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.processing_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = NavyDeep
            )

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedContent(
                targetState = currentMessage,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ProgressMessageTransition"
            ) { targetMsg ->
                Text(
                    text = targetMsg,
                    fontSize = 14.sp,
                    color = SlateTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Step Progress Cards
            Column(
                modifier = Modifier.fillMaxWidth(0.85f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StepRow(title = "1. Safe image loading", isCompleted = stepIndex > 1, isCurrent = stepIndex == 1)
                StepRow(title = "2. Face centering & crop", isCompleted = stepIndex > 2, isCurrent = stepIndex == 2)
                StepRow(title = "3. Clean white background", isCompleted = stepIndex > 3, isCurrent = stepIndex == 3)
                StepRow(title = "4. Guaranteed KB compression", isCompleted = stepIndex > 4, isCurrent = stepIndex == 4)
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Privacy Subtitle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color(0xFFEFF6FF), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = EmeraldSuccess,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.processing_hint),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = SlateTextSecondary
                )
            }
        }
    }
}

@Composable
private fun StepRow(title: String, isCompleted: Boolean, isCurrent: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCompleted -> EmeraldSuccess
                        isCurrent -> BlueAccent
                        else -> Color(0xFFCBD5E1)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = if (isCurrent || isCompleted) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isCurrent || isCompleted) SlateTextPrimary else Color(0xFF94A3B8)
        )
    }
}

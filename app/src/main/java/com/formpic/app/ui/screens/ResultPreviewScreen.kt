package com.formpic.app.ui.screens

import android.app.Activity
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formpic.app.R
import com.formpic.app.data.model.ProcessingResult
import com.formpic.app.ui.components.BeforeAfterSlider
import com.formpic.app.ui.components.FormPicTopAppBar
import com.formpic.app.ui.theme.AmberBg
import com.formpic.app.ui.theme.AmberWarning
import com.formpic.app.ui.theme.BlueAccent
import com.formpic.app.ui.theme.EmeraldBg
import com.formpic.app.ui.theme.EmeraldSuccess
import com.formpic.app.ui.theme.NavyDeep
import com.formpic.app.ui.theme.SlateBorder
import com.formpic.app.ui.theme.SlateTextPrimary
import com.formpic.app.ui.theme.SlateTextSecondary

@Composable
fun ResultPreviewScreen(
    result: ProcessingResult,
    onDownloadClick: (Activity) -> Unit,
    onShareClick: () -> Unit,
    onCreateAnotherClick: () -> Unit,
    onFineTuneClick: () -> Unit,
    onNavigateHome: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    Scaffold(
        topBar = {
            FormPicTopAppBar(
                title = stringResource(R.string.result_title),
                canNavigateBack = true,
                onNavigateBack = onNavigateHome
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Target satisfied badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .background(EmeraldBg)
                    .border(1.dp, EmeraldSuccess.copy(alpha = 0.3f), RoundedCornerShape(30.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(EmeraldSuccess),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Strictly Under ${result.targetMaxKb} KB ✓",
                    color = EmeraldSuccess,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interactive Before & After photo preview
            BeforeAfterSlider(
                originalBitmap = result.originalBitmap,
                processedBitmap = result.finalBitmap,
                aspectRatio = result.preset.aspectRatio
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Photo Metadata Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    MetadataRow(
                        label = "Final File Size",
                        value = "${result.formattedFileSize} (Target: <= ${result.targetMaxKb} KB)",
                        isHighlighted = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    MetadataRow(
                        label = "Dimensions",
                        value = "${result.width} × ${result.height} px"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    MetadataRow(
                        label = "File Format",
                        value = "JPG (Standard Web & Form)"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    MetadataRow(
                        label = "Requirement",
                        value = result.preset.title
                    )
                }
            }

            // Warnings or Insights
            if (result.qualityReport.warnings.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AmberBg),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = AmberWarning,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            result.qualityReport.warnings.forEach { warning ->
                                Text(
                                    text = warning,
                                    fontSize = 12.sp,
                                    color = Color(0xFF92400E)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Download Button (Triggers AdMob Interstitial then saves)
            Button(
                onClick = {
                    if (activity != null) {
                        onDownloadClick(activity)
                    } else {
                        Toast.makeText(context, "Ready to download", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.download_photo),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary Share Button
            OutlinedButton(
                onClick = onShareClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyDeep)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.share),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Create Another Photo Button
            OutlinedButton(
                onClick = onCreateAnotherClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SlateTextSecondary)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.create_another),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun MetadataRow(label: String, value: String, isHighlighted: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = SlateTextSecondary
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isHighlighted) EmeraldSuccess else SlateTextPrimary
        )
    }
}

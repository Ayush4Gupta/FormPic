package com.formpic.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formpic.app.R
import com.formpic.app.data.model.PhotoPreset
import com.formpic.app.data.model.PresetCategory
import com.formpic.app.data.repository.PresetRepository
import com.formpic.app.ui.components.FormPicTopAppBar
import com.formpic.app.ui.theme.BlueAccent
import com.formpic.app.ui.theme.BlueSoftBg
import com.formpic.app.ui.theme.EmeraldBg
import com.formpic.app.ui.theme.EmeraldSuccess
import com.formpic.app.ui.theme.NavyDeep
import com.formpic.app.ui.theme.SlateBorder
import com.formpic.app.ui.theme.SlateTextPrimary
import com.formpic.app.ui.theme.SlateTextSecondary

@Composable
fun HomeScreen(
    selectedPreset: PhotoPreset,
    whiteBackgroundEnabled: Boolean,
    passportSizeEnabled: Boolean,
    onPresetSelected: (PhotoPreset) -> Unit,
    onToggleWhiteBackground: (Boolean) -> Unit,
    onTogglePassportSize: (Boolean) -> Unit,
    onNavigateToCamera: () -> Unit,
    onPhotoSelected: (Uri, PhotoPreset) -> Unit,
    onNavigateToPresets: (tabIndex: Int) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHelp: () -> Unit
) {
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onPhotoSelected(it, selectedPreset) }
    }

    Scaffold(
        topBar = {
            FormPicTopAppBar(
                title = stringResource(R.string.app_name),
                onSettingsClick = onNavigateToSettings,
                onHelpClick = onNavigateToHelp
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
            // Dynamic Hero Card ("Active Target Command Center")
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = NavyDeep),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E3A5F))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldSuccess)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (selectedPreset.isSignature) "SIGNATURE TARGET" else "ACTIVE TARGET",
                                    color = Color(0xFF60A5FA),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        Text(
                            text = "Change Spec ⚙",
                            color = Color(0xFF93C5FD),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable {
                                val targetTab = if (selectedPreset.category == PresetCategory.GOVERNMENT_EXAMS) 1 else 0
                                onNavigateToPresets(targetTab)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = selectedPreset.title,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = selectedPreset.subtitle,
                        color = Color(0xFFCBD5E1),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Spec Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1E293B))
                                .padding(vertical = 8.dp, horizontal = 10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "MAX SIZE",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "≤ ${selectedPreset.targetMaxKb} KB",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1.1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1E293B))
                                .padding(vertical = 8.dp, horizontal = 10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "FRAMING",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = when {
                                        selectedPreset.isSignature -> "Signature Ink"
                                        passportSizeEnabled && selectedPreset.widthMm > 0 -> "${selectedPreset.widthMm.toInt()}×${selectedPreset.heightMm.toInt()} mm"
                                        passportSizeEnabled -> "${selectedPreset.widthPx}×${selectedPreset.heightPx} px"
                                        else -> "Original Ratio"
                                    },
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1E293B))
                                .padding(vertical = 8.dp, horizontal = 10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "BACKGROUND",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = when {
                                        selectedPreset.isSignature -> "White Paper"
                                        whiteBackgroundEnabled -> "Pure White"
                                        else -> "Original BG"
                                    },
                                    color = if (whiteBackgroundEnabled && !selectedPreset.isSignature) EmeraldSuccess else Color(0xFF93C5FD),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Command Center Controls: Framing & Background Modes
            if (selectedPreset.isSignature) {
                // Signature Mode Banner: Informs user face detection & background removal are bypassed
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "✒️", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Signature Mode Active",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E40AF)
                            )
                            Text(
                                text = "Face detection and eye checks are bypassed. Your signature ink strokes are preserved cleanly on white paper under ${selectedPreset.targetMaxKb} KB.",
                                fontSize = 11.sp,
                                color = Color(0xFF1D4ED8),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Framing Mode Switcher (Passport Size 3.5×4.5 cm vs Original Photo)
                    if (selectedPreset.isPassportSizeMandatory) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onTogglePassportSize(!passportSizeEnabled) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (passportSizeEnabled) Color(0xFFF0FDF4) else Color(0xFFFFFBEB)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (passportSizeEnabled) Color(0xFFBBF7D0) else Color(0xFFFDE68A)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = if (passportSizeEnabled) "📐" else "🖼️", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (passportSizeEnabled) "Passport Size (3.5×4.5 cm): Mandated" else "Original Photo Framing Selected",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (passportSizeEnabled) Color(0xFF166534) else Color(0xFF92400E)
                                    )
                                    Text(
                                        text = if (passportSizeEnabled) "Tap to preserve original photo framing without crop." else "Tap to restore mandatory 3.5×4.5 cm passport crop.",
                                        fontSize = 11.sp,
                                        color = if (passportSizeEnabled) Color(0xFF15803D) else Color(0xFFB45309)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (passportSizeEnabled) EmeraldSuccess else Color(0xFFD97706))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (passportSizeEnabled) "ON ✓" else "OFF",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    } else {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFE2E8F0).copy(alpha = 0.6f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Option 1: Passport Size Crop (3.5 × 4.5 cm)
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(9.dp))
                                        .clickable { onTogglePassportSize(true) },
                                    color = if (passportSizeEnabled) Color.White else Color.Transparent,
                                    shape = RoundedCornerShape(9.dp),
                                    border = if (passportSizeEnabled) androidx.compose.foundation.BorderStroke(1.5.dp, BlueAccent) else null
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 9.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "📐 Passport (3.5×4.5 cm)",
                                            fontSize = 11.sp,
                                            fontWeight = if (passportSizeEnabled) FontWeight.Bold else FontWeight.Medium,
                                            color = if (passportSizeEnabled) BlueAccent else SlateTextSecondary
                                        )
                                    }
                                }

                                // Option 2: Original Photo Framing
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(9.dp))
                                        .clickable { onTogglePassportSize(false) },
                                    color = if (!passportSizeEnabled) Color.White else Color.Transparent,
                                    shape = RoundedCornerShape(9.dp),
                                    border = if (!passportSizeEnabled) androidx.compose.foundation.BorderStroke(1.dp, SlateBorder) else null
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 9.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "🖼️ Original Framing",
                                            fontSize = 11.sp,
                                            fontWeight = if (!passportSizeEnabled) FontWeight.Bold else FontWeight.Medium,
                                            color = if (!passportSizeEnabled) NavyDeep else SlateTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Background Mode Switcher (Pure White vs Original Background)
                    if (selectedPreset.isWhiteBackgroundMandatory) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onToggleWhiteBackground(!whiteBackgroundEnabled) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (whiteBackgroundEnabled) Color(0xFFECFDF5) else Color(0xFFFFFBEB)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (whiteBackgroundEnabled) Color(0xFFA7F3D0) else Color(0xFFFDE68A)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (whiteBackgroundEnabled) "🛡️" else "⚠️",
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (whiteBackgroundEnabled) "Pure White BG: Portal Mandated" else "Original BG Selected (Exams require White)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (whiteBackgroundEnabled) Color(0xFF065F46) else Color(0xFF92400E)
                                    )
                                    Text(
                                        text = if (whiteBackgroundEnabled) "Tap to keep original background if already studio white." else "Tap to restore mandatory pure white background.",
                                        fontSize = 11.sp,
                                        color = if (whiteBackgroundEnabled) Color(0xFF047857) else Color(0xFFB45309)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (whiteBackgroundEnabled) EmeraldSuccess else Color(0xFFD97706))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (whiteBackgroundEnabled) "ON ✓" else "OFF",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    } else {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFE2E8F0).copy(alpha = 0.6f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Option 1: Keep Original Background
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(9.dp))
                                        .clickable { onToggleWhiteBackground(false) },
                                    color = if (!whiteBackgroundEnabled) Color.White else Color.Transparent,
                                    shape = RoundedCornerShape(9.dp),
                                    border = if (!whiteBackgroundEnabled) androidx.compose.foundation.BorderStroke(1.dp, SlateBorder) else null
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 9.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "⚪ Keep Original BG",
                                            fontSize = 11.sp,
                                            fontWeight = if (!whiteBackgroundEnabled) FontWeight.Bold else FontWeight.Medium,
                                            color = if (!whiteBackgroundEnabled) NavyDeep else SlateTextSecondary
                                        )
                                    }
                                }

                                // Option 2: AI Pure White Background
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(9.dp))
                                        .clickable { onToggleWhiteBackground(true) },
                                    color = if (whiteBackgroundEnabled) Color.White else Color.Transparent,
                                    shape = RoundedCornerShape(9.dp),
                                    border = if (whiteBackgroundEnabled) androidx.compose.foundation.BorderStroke(1.5.dp, EmeraldSuccess) else null
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 9.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "🪄 Pure White (AI)",
                                            fontSize = 11.sp,
                                            fontWeight = if (whiteBackgroundEnabled) FontWeight.Bold else FontWeight.Medium,
                                            color = if (whiteBackgroundEnabled) EmeraldSuccess else SlateTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary Action Buttons
            Button(
                onClick = onNavigateToCamera,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BlueAccent)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.take_photo),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    galleryLauncher.launch("image/*")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyDeep)
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.choose_gallery),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Quick Target Size Presets Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.quick_targets),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextPrimary
                )
                Text(
                    text = "All Sizes →",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BlueAccent,
                    modifier = Modifier.clickable { onNavigateToPresets(0) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Size Pills (Reactive Selection Fix)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PresetRepository.quickKbPresets.forEach { preset ->
                    val isSelected = preset.id == selectedPreset.id
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(78.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                onPresetSelected(preset)
                            },
                        color = if (isSelected) BlueSoftBg else Color.White,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) BlueAccent else SlateBorder
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(6.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = preset.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (isSelected) BlueAccent else SlateTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(BlueAccent)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Active ✓",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            } else {
                                Text(
                                    text = if (preset.id == "quick_50kb") "Popular" else "Select",
                                    fontSize = 10.sp,
                                    color = SlateTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Official Indian Exam Presets Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Official Indian Exams",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextPrimary
                )
                Text(
                    text = "View All (7+) →",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BlueAccent,
                    modifier = Modifier.clickable { onNavigateToPresets(1) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4 Top Popular Exam Cards (2x2 Grid)
            val topExams = listOf(
                PresetRepository.officialExamPresets[1], // SSC
                PresetRepository.officialExamPresets[2], // UPSC
                PresetRepository.officialExamPresets[3], // IBPS
                PresetRepository.officialExamPresets[0]  // Passport Seva
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (i in 0 until topExams.size step 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (j in 0..1) {
                            val exam = topExams[i + j]
                            val isExamSelected = exam.id == selectedPreset.id
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onPresetSelected(exam) },
                                color = if (isExamSelected) BlueSoftBg else Color.White,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isExamSelected) 2.dp else 1.dp,
                                    if (isExamSelected) BlueAccent else SlateBorder
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = when (exam.id) {
                                                "official_ssc" -> "SSC Exams"
                                                "official_upsc" -> "UPSC Exams"
                                                "official_ibps_sbi" -> "IBPS / Bank"
                                                "official_passport_india" -> "Passport Seva"
                                                else -> exam.title
                                            },
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isExamSelected) BlueAccent else SlateTextPrimary
                                        )
                                        if (isExamSelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = BlueAccent,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Max ${exam.targetMaxKb} KB • ${exam.widthMm.toInt()}×${exam.heightMm.toInt()} mm",
                                        fontSize = 11.sp,
                                        color = if (isExamSelected) BlueAccent else SlateTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Official Indian Presets Banner Card (Navigates to Tab 1)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToPresets(1) },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(EmeraldBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Explore All Exam Presets",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SlateTextPrimary
                        )
                        Text(
                            text = "NEET UG, RRB Railway, PAN Card, State PSCs…",
                            fontSize = 12.sp,
                            color = SlateTextSecondary
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Custom Requirements Card (Navigates to Tab 2)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToPresets(2) },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = BlueAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Need Custom Dimensions or KB Limit?",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SlateTextPrimary
                        )
                        Text(
                            text = "Set exact pixels, height, width & file size",
                            fontSize = 12.sp,
                            color = SlateTextSecondary
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Privacy Guarantee Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = EmeraldSuccess,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.privacy_badge),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = SlateTextSecondary
                )
            }
        }
    }
}


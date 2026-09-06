package com.formpic.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formpic.app.R
import com.formpic.app.data.model.PhotoPreset
import com.formpic.app.data.repository.PresetRepository
import com.formpic.app.ui.components.FormPicTopAppBar
import com.formpic.app.ui.theme.BlueAccent
import com.formpic.app.ui.theme.BlueSoftBg
import com.formpic.app.ui.theme.EmeraldSuccess
import com.formpic.app.ui.theme.NavyDeep
import com.formpic.app.ui.theme.SlateBorder
import com.formpic.app.ui.theme.SlateTextPrimary
import com.formpic.app.ui.theme.SlateTextSecondary

@Composable
fun PresetSelectionScreen(
    currentPreset: PhotoPreset,
    initialTabIndex: Int = 0,
    onPresetSelected: (PhotoPreset) -> Unit,
    onNavigateBack: () -> Unit
) {
    var selectedTabIndex by remember(initialTabIndex) { mutableIntStateOf(initialTabIndex) }
    var selectedPreset by remember(currentPreset) { mutableStateOf(currentPreset) }

    // Custom form states
    var customKbText by remember { mutableStateOf("45") }
    var customWidthText by remember { mutableStateOf("600") }
    var customHeightText by remember { mutableStateOf("750") }

    Scaffold(
        topBar = {
            FormPicTopAppBar(
                title = stringResource(R.string.preset_screen_title),
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
                    onClick = {
                        if (selectedTabIndex == 2) {
                            val kb = customKbText.toIntOrNull() ?: 50
                            val w = customWidthText.toIntOrNull() ?: 600
                            val h = customHeightText.toIntOrNull() ?: 750
                            val custom = PresetRepository.createCustomPreset(kb, w, h)
                            onPresetSelected(custom)
                        } else {
                            onPresetSelected(selectedPreset)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyDeep)
                ) {
                    Text(
                        text = if (selectedTabIndex == 2) {
                            "Apply Custom (${customKbText.ifBlank { "50" }} KB)"
                        } else {
                            "Apply ${selectedPreset.title}"
                        },
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
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = NavyDeep,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = BlueAccent
                    )
                }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Text(
                            text = stringResource(R.string.tab_quick_kb),
                            fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Text(
                            text = stringResource(R.string.tab_government_exams),
                            fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = {
                        Text(
                            text = stringResource(R.string.tab_custom),
                            fontWeight = if (selectedTabIndex == 2) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
            }

            when (selectedTabIndex) {
                0 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(PresetRepository.quickKbPresets) { preset ->
                            PresetCard(
                                preset = preset,
                                isSelected = selectedPreset.id == preset.id,
                                onClick = { selectedPreset = preset }
                            )
                        }
                    }
                }
                1 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "Official Exam Photo Presets",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDeep
                            )
                        }

                        items(PresetRepository.officialExamPresets) { preset ->
                            PresetCard(
                                preset = preset,
                                isSelected = selectedPreset.id == preset.id,
                                onClick = { selectedPreset = preset }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Official Signature Presets (White Paper / Ink)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDeep
                            )
                            Text(
                                text = "Pure white background removal is bypassed to preserve clean, dark ink strokes.",
                                fontSize = 12.sp,
                                color = SlateTextSecondary,
                                modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
                            )
                        }

                        items(PresetRepository.officialSignaturePresets) { preset ->
                            PresetCard(
                                preset = preset,
                                isSelected = selectedPreset.id == preset.id,
                                onClick = { selectedPreset = preset }
                            )
                        }
                    }
                }
                2 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Tune,
                                        contentDescription = null,
                                        tint = BlueAccent,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Custom Photo Parameters",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SlateTextPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                val textFieldColors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = SlateTextPrimary,
                                    unfocusedTextColor = SlateTextPrimary,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    cursorColor = BlueAccent,
                                    focusedBorderColor = BlueAccent,
                                    unfocusedBorderColor = SlateBorder,
                                    focusedLabelColor = BlueAccent,
                                    unfocusedLabelColor = SlateTextSecondary,
                                    focusedSupportingTextColor = SlateTextSecondary,
                                    unfocusedSupportingTextColor = SlateTextSecondary
                                )
                                val inputTextStyle = androidx.compose.ui.text.TextStyle(
                                    color = SlateTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                OutlinedTextField(
                                    value = customKbText,
                                    onValueChange = { customKbText = it.filter { ch -> ch.isDigit() } },
                                    label = { Text("Target Maximum File Size (KB)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = inputTextStyle,
                                    supportingText = { Text("Guaranteed strict <= target limit") },
                                    colors = textFieldColors
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedTextField(
                                        value = customWidthText,
                                        onValueChange = { customWidthText = it.filter { ch -> ch.isDigit() } },
                                        label = { Text("Width (px)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        textStyle = inputTextStyle,
                                        modifier = Modifier.weight(1f),
                                        colors = textFieldColors
                                    )
                                    OutlinedTextField(
                                        value = customHeightText,
                                        onValueChange = { customHeightText = it.filter { ch -> ch.isDigit() } },
                                        label = { Text("Height (px)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        textStyle = inputTextStyle,
                                        modifier = Modifier.weight(1f),
                                        colors = textFieldColors
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetCard(
    preset: PhotoPreset,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) BlueSoftBg else Color.White
        ),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) BlueAccent else SlateBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = preset.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextPrimary
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) BlueAccent else Color(0xFFF1F5F9))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "<= ${preset.targetMaxKb} KB",
                            color = if (isSelected) Color.White else SlateTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = preset.subtitle,
                    fontSize = 13.sp,
                    color = SlateTextSecondary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Framing Status Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when {
                                    preset.isSignature -> Color(0xFFF1F5F9)
                                    preset.isPassportSize && preset.isPassportSizeMandatory -> Color(0xFFEFF6FF)
                                    preset.isPassportSize -> Color(0xFFF1F5F9)
                                    else -> Color(0xFFF8FAFC)
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = when {
                                preset.isSignature -> "✒️ Signature Ratio"
                                preset.isPassportSize && preset.isPassportSizeMandatory -> "📐 3.5×4.5 cm Mandated"
                                preset.isPassportSize -> "📐 3.5×4.5 cm Passport"
                                preset.isSquareAspect -> "🔲 1:1 Square"
                                else -> "🖼️ Flexible Framing"
                            },
                            color = when {
                                preset.isPassportSize && preset.isPassportSizeMandatory -> Color(0xFF1D4ED8)
                                else -> NavyDeep
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Background status pill
                    if (!preset.isSignature) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (preset.isWhiteBackgroundMandatory) Color(0xFFDCFCE7) else Color(0xFFF1F5F9)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (preset.isWhiteBackgroundMandatory) "🛡️ White BG Mandated" else "⚪ White BG Optional",
                                color = if (preset.isWhiteBackgroundMandatory) Color(0xFF166534) else SlateTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    if (preset.officialSource.isNotEmpty()) {
                        Text(
                            text = "• ${preset.officialSource.take(24)}…",
                            fontSize = 10.sp,
                            color = Color(0xFF059669),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(BlueAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

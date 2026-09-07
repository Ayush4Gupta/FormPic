package com.validpic.app.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.validpic.app.R
import com.validpic.app.engine.SaveFolderOption
import com.validpic.app.engine.StorageManager
import com.validpic.app.ui.components.FeedbackDialog
import com.validpic.app.ui.components.ValidPicTopAppBar
import com.validpic.app.ui.theme.BlueAccent
import com.validpic.app.ui.theme.BlueSoftBg
import com.validpic.app.ui.theme.NavyDeep
import com.validpic.app.ui.theme.SlateBorder
import com.validpic.app.ui.theme.SlateTextPrimary
import com.validpic.app.ui.theme.SlateTextSecondary

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToHelp: () -> Unit
) {
    val context = LocalContext.current
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var showFolderDialog by remember { mutableStateOf(false) }
    var currentSaveOption by remember { mutableStateOf(StorageManager.getSaveFolderOption(context)) }

    if (showFeedbackDialog) {
        FeedbackDialog(onDismiss = { showFeedbackDialog = false })
    }

    if (showFolderDialog) {
        FolderSelectionDialog(
            currentOption = currentSaveOption,
            onSelectOption = { option ->
                StorageManager.setSaveFolderOption(context, option)
                currentSaveOption = option
                Toast.makeText(context, "Save location set to ${option.relativeSubpath}", Toast.LENGTH_SHORT).show()
            },
            onOpenFolder = {
                StorageManager.openSavedPhotosFolder(context)
            },
            onDismiss = { showFolderDialog = false }
        )
    }

    Scaffold(
        topBar = {
            ValidPicTopAppBar(
                title = stringResource(R.string.settings_title),
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Storage & Hygiene
            SettingsSectionHeader(title = "STORAGE & FILES")

            SettingsItemCard(
                icon = Icons.Default.Folder,
                title = "Photo Save Location",
                subtitle = "${currentSaveOption.relativeSubpath} • Tap to change",
                onClick = { showFolderDialog = true }
            )

            SettingsItemCard(
                icon = Icons.Default.CleaningServices,
                title = stringResource(R.string.pref_clear_cache),
                subtitle = stringResource(R.string.pref_clear_cache_desc),
                onClick = {
                    StorageManager.clearTemporaryCache(context)
                    Toast.makeText(context, context.getString(R.string.cache_cleared_msg), Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Support & Information
            SettingsSectionHeader(title = "SUPPORT & LEGAL")

            SettingsItemCard(
                icon = Icons.Default.Email,
                title = stringResource(R.string.pref_feedback_title),
                subtitle = stringResource(R.string.pref_feedback_desc),
                onClick = { showFeedbackDialog = true }
            )

            SettingsItemCard(
                icon = Icons.Default.HelpOutline,
                title = stringResource(R.string.help_faq),
                subtitle = "Answers to exam photo limits & camera tips",
                onClick = onNavigateToHelp
            )

            SettingsItemCard(
                icon = Icons.Default.PrivacyTip,
                title = stringResource(R.string.privacy_policy),
                subtitle = "100% on-device processing guarantee",
                onClick = onNavigateToPrivacy
            )

            SettingsItemCard(
                icon = Icons.Default.StarRate,
                title = stringResource(R.string.rate_app),
                subtitle = "Help other applicants discover ValidPic",
                onClick = {
                    openPlayStore(context)
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // About Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.about_title),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDeep
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.about_desc),
                        fontSize = 13.sp,
                        color = SlateTextSecondary,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Version 1.0.7 (Build 8)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B)
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFEFF6FF)
                        ) {
                            Text(
                                text = "100% Offline AI",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BlueAccent
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FolderSelectionDialog(
    currentOption: SaveFolderOption,
    onSelectOption: (SaveFolderOption) -> Unit,
    onOpenFolder: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Photo Save Location",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDeep
                        )
                        Text(
                            text = "Select where ValidPic stores your processed photos",
                            fontSize = 12.sp,
                            color = SlateTextSecondary
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = SlateTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SaveFolderOption.values().forEach { option ->
                        val isSelected = option == currentOption
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onSelectOption(option) },
                            color = if (isSelected) BlueSoftBg else Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) BlueAccent else SlateBorder
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = option.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) BlueAccent else NavyDeep
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = option.description,
                                        fontSize = 11.sp,
                                        color = SlateTextSecondary,
                                        lineHeight = 15.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "📁 ${option.relativeSubpath}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSelected) BlueAccent else Color(0xFF64748B)
                                    )
                                }
                                if (isSelected) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = BlueAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedButton(
                    onClick = onOpenFolder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyDeep)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Open ValidPic Folder in Gallery", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyDeep)
                ) {
                    Text(text = "Done", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF64748B),
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
    )
}

@Composable
private fun SettingsItemCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NavyDeep,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SlateTextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = SlateTextSecondary
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFFCBD5E1)
            )
        }
    }
}

private fun openPlayStore(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
        context.startActivity(intent)
    } catch (e: Exception) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}"))
        context.startActivity(intent)
    }
}

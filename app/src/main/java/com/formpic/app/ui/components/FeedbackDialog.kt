package com.formpic.app.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.formpic.app.R
import com.formpic.app.ui.theme.BlueAccent
import com.formpic.app.ui.theme.EmeraldSuccess
import com.formpic.app.ui.theme.NavyDeep
import com.formpic.app.ui.theme.SlateBorder
import com.formpic.app.ui.theme.SlateTextPrimary
import com.formpic.app.ui.theme.SlateTextSecondary

enum class FeedbackCategory(val title: String, val icon: String) {
    REQUEST_EXAM("Request Exam Preset", "📑"),
    PORTAL_ISSUE("Report Portal Issue", "🐞"),
    FEATURE_SUGGESTION("Feature Suggestion", "💡"),
    GENERAL("General Feedback", "⭐")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FeedbackDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf(FeedbackCategory.REQUEST_EXAM) }
    var messageText by remember { mutableStateOf("") }
    var contactEmail by remember { mutableStateOf("") }

    val appVersionName = "1.0.5"
    val appVersionCode = 6

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header with Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.feedback_dialog_title),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDeep
                        )
                        Text(
                            text = stringResource(R.string.feedback_dialog_subtitle),
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

                Spacer(modifier = Modifier.height(14.dp))

                // Category Selector Chips
                Text(
                    text = "SELECT TOPIC",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FeedbackCategory.entries.forEach { category ->
                        val isSelected = selectedCategory == category
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedCategory = category },
                            color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) BlueAccent else SlateBorder
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = category.icon, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = category.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) BlueAccent else SlateTextPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Text Input Style
                val textFieldColors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = SlateTextPrimary,
                    unfocusedTextColor = SlateTextPrimary,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = BlueAccent,
                    focusedBorderColor = BlueAccent,
                    unfocusedBorderColor = SlateBorder,
                    focusedLabelColor = BlueAccent,
                    unfocusedLabelColor = SlateTextSecondary
                )

                // Message Text Field
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { if (it.length <= 600) messageText = it },
                    label = { Text("Your Message / Specifications") },
                    placeholder = {
                        Text(
                            text = when (selectedCategory) {
                                FeedbackCategory.REQUEST_EXAM -> "Specify Exam Name (e.g., UPPSC, MPSC, RPF), exact photo/signature KB limits, and pixel dimensions..."
                                FeedbackCategory.PORTAL_ISSUE -> "Describe what error the portal showed, portal name, and what went wrong..."
                                FeedbackCategory.FEATURE_SUGGESTION -> "Tell us what feature would make FormPic more useful for your application..."
                                FeedbackCategory.GENERAL -> "Share your thoughts or experience using FormPic..."
                            },
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp),
                    supportingText = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "${messageText.length} / 600",
                                fontSize = 11.sp,
                                color = SlateTextSecondary
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Optional Email Input
                OutlinedTextField(
                    value = contactEmail,
                    onValueChange = { contactEmail = it },
                    label = { Text(stringResource(R.string.feedback_email_hint)) },
                    placeholder = { Text("example@gmail.com", color = Color(0xFF94A3B8), fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Diagnostic Info Pill
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "ℹ️", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "FormPic v$appVersionName (code $appVersionCode) • Android ${Build.VERSION.RELEASE} • ${Build.MANUFACTURER} ${Build.MODEL}",
                            fontSize = 11.sp,
                            color = SlateTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                val fullFeedbackBody = buildString {
                    appendLine("Category: ${selectedCategory.title}")
                    if (contactEmail.isNotBlank()) {
                        appendLine("Contact Email: $contactEmail")
                    }
                    appendLine()
                    appendLine("--- Feedback Message ---")
                    appendLine(messageText.ifBlank { "(No message provided)" })
                    appendLine()
                    appendLine("--- Device Diagnostics ---")
                    appendLine("App Version: FormPic v$appVersionName (Build $appVersionCode)")
                    appendLine("OS Version: Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
                    appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
                }

                // Primary Button: Send via Email
                Button(
                    onClick = {
                        if (messageText.isBlank()) {
                            Toast.makeText(context, "Please enter your feedback message first", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:support@formpic.app")
                            putExtra(Intent.EXTRA_SUBJECT, "[FormPic Feedback] ${selectedCategory.title}")
                            putExtra(Intent.EXTRA_TEXT, fullFeedbackBody)
                        }

                        try {
                            context.startActivity(Intent.createChooser(intent, context.getString(R.string.feedback_email_intent_chooser)))
                            onDismiss()
                        } catch (e: Exception) {
                            // Fallback: Copy to clipboard if no email client is found
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("FormPic Feedback", fullFeedbackBody))
                            Toast.makeText(
                                context,
                                "No email app found. Feedback copied to clipboard! Email to support@formpic.app",
                                Toast.LENGTH_LONG
                            ).show()
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyDeep)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mail,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.feedback_send_button),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Secondary Button: Copy to Clipboard
                OutlinedButton(
                    onClick = {
                        if (messageText.isBlank()) {
                            Toast.makeText(context, "Please enter your message first", Toast.LENGTH_SHORT).show()
                            return@OutlinedButton
                        }
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("FormPic Feedback", fullFeedbackBody))
                        Toast.makeText(context, context.getString(R.string.feedback_copied_msg), Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SlateTextSecondary)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.feedback_copy_button),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

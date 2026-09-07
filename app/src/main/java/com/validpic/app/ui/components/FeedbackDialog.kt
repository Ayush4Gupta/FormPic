package com.validpic.app.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.validpic.app.BuildConfig
import com.validpic.app.R
import com.validpic.app.data.repository.FeedbackManager
import com.validpic.app.data.repository.FeedbackSubmitResult
import com.validpic.app.ui.theme.AmberWarning
import com.validpic.app.ui.theme.BlueAccent
import com.validpic.app.ui.theme.EmeraldSuccess
import com.validpic.app.ui.theme.NavyDeep
import com.validpic.app.ui.theme.SlateBorder
import com.validpic.app.ui.theme.SlateTextPrimary
import com.validpic.app.ui.theme.SlateTextSecondary
import kotlinx.coroutines.launch

enum class FeedbackCategory(val title: String, val icon: String) {
    REQUEST_EXAM("Request Exam Preset", "📑"),
    PORTAL_ISSUE("Report Portal Issue", "🐞"),
    FEATURE_SUGGESTION("Feature Suggestion", "💡"),
    GENERAL("General Feedback", "⭐")
}

private enum class FeedbackDialogState {
    INPUT,
    SUBMITTING,
    SUCCESS,
    FALLBACK
}

@Composable
fun FeedbackDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedCategory by remember { mutableStateOf(FeedbackCategory.REQUEST_EXAM) }
    var messageText by remember { mutableStateOf("") }
    var contactEmail by remember { mutableStateOf("") }
    var dialogState by remember { mutableStateOf(FeedbackDialogState.INPUT) }
    var failureReason by remember { mutableStateOf("") }

    val appVersionName = BuildConfig.VERSION_NAME
    val appVersionCode = BuildConfig.VERSION_CODE

    val fullFeedbackBody = remember(selectedCategory, contactEmail, messageText) {
        buildString {
            appendLine("Category: ${selectedCategory.title}")
            if (contactEmail.isNotBlank()) {
                appendLine("Contact Email: $contactEmail")
            }
            appendLine()
            appendLine("--- Feedback Message ---")
            appendLine(messageText.ifBlank { "(No message provided)" })
            appendLine()
            appendLine("--- Device Diagnostics ---")
            appendLine("App Version: ValidPic v$appVersionName (Build $appVersionCode)")
            appendLine("OS Version: Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
        }
    }

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

                AnimatedContent(
                    targetState = dialogState,
                    label = "FeedbackDialogTransition"
                ) { state ->
                    when (state) {
                        FeedbackDialogState.INPUT -> {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Category Selector
                                Text(
                                    text = "SELECT TOPIC",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B),
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val categories = FeedbackCategory.values()
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        CategoryChip(
                                            category = categories[0],
                                            isSelected = selectedCategory == categories[0],
                                            modifier = Modifier.weight(1f),
                                            onClick = { selectedCategory = categories[0] }
                                        )
                                        CategoryChip(
                                            category = categories[1],
                                            isSelected = selectedCategory == categories[1],
                                            modifier = Modifier.weight(1f),
                                            onClick = { selectedCategory = categories[1] }
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        CategoryChip(
                                            category = categories[2],
                                            isSelected = selectedCategory == categories[2],
                                            modifier = Modifier.weight(1f),
                                            onClick = { selectedCategory = categories[2] }
                                        )
                                        CategoryChip(
                                            category = categories[3],
                                            isSelected = selectedCategory == categories[3],
                                            modifier = Modifier.weight(1f),
                                            onClick = { selectedCategory = categories[3] }
                                        )
                                    }
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
                                    unfocusedLabelColor = SlateTextSecondary
                                )

                                val inputTextStyle = androidx.compose.ui.text.TextStyle(
                                    color = SlateTextPrimary,
                                    fontSize = 14.sp
                                )

                                // Message Text Field
                                OutlinedTextField(
                                    value = messageText,
                                    onValueChange = { if (it.length <= 600) messageText = it },
                                    textStyle = inputTextStyle,
                                    label = { Text("Your Message / Specifications") },
                                    placeholder = {
                                        Text(
                                            text = when (selectedCategory) {
                                                FeedbackCategory.REQUEST_EXAM -> "Specify Exam Name (e.g. UPPSC, MPSC, RPF), exact photo/signature KB limits, and pixel dimensions..."
                                                FeedbackCategory.PORTAL_ISSUE -> "Describe what error the portal showed, portal name, and what went wrong..."
                                                FeedbackCategory.FEATURE_SUGGESTION -> "Tell us what feature would make ValidPic more useful for your application..."
                                                FeedbackCategory.GENERAL -> "Share your thoughts or experience using ValidPic..."
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
                                    textStyle = inputTextStyle,
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
                                            text = "ValidPic v$appVersionName (code $appVersionCode) • Android ${Build.VERSION.RELEASE} • ${Build.MANUFACTURER} ${Build.MODEL}",
                                            fontSize = 11.sp,
                                            color = SlateTextSecondary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Primary In-App Submit Button
                                Button(
                                    onClick = {
                                        if (messageText.isBlank()) {
                                            Toast.makeText(context, "Please describe your feedback or preset request", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }

                                        dialogState = FeedbackDialogState.SUBMITTING
                                        coroutineScope.launch {
                                            val result = FeedbackManager.submitFeedback(
                                                category = selectedCategory.title,
                                                message = messageText,
                                                email = contactEmail,
                                                versionName = appVersionName,
                                                versionCode = appVersionCode
                                            )
                                            when (result) {
                                                is FeedbackSubmitResult.Success -> {
                                                    dialogState = FeedbackDialogState.SUCCESS
                                                }
                                                is FeedbackSubmitResult.Failure -> {
                                                    failureReason = result.errorMessage
                                                    dialogState = FeedbackDialogState.FALLBACK
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = NavyDeep)
                                ) {
                                    Text(
                                        text = "Submit Feedback",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Subtle Alternate Actions Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(
                                        onClick = {
                                            if (messageText.isBlank()) {
                                                Toast.makeText(context, "Please enter your message first", Toast.LENGTH_SHORT).show()
                                                return@TextButton
                                            }
                                            sendViaEmailIntent(context, selectedCategory.title, fullFeedbackBody)
                                            onDismiss()
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Email,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = SlateTextSecondary
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Email Directly",
                                            fontSize = 12.sp,
                                            color = SlateTextSecondary
                                        )
                                    }

                                    TextButton(
                                        onClick = {
                                            if (messageText.isBlank()) {
                                                Toast.makeText(context, "Please enter your message first", Toast.LENGTH_SHORT).show()
                                                return@TextButton
                                            }
                                            copyToClipboard(context, fullFeedbackBody)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = SlateTextSecondary
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Copy Text",
                                            fontSize = 12.sp,
                                            color = SlateTextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        FeedbackDialogState.SUBMITTING -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 36.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    color = NavyDeep,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(42.dp)
                                )
                                Spacer(modifier = Modifier.height(18.dp))
                                Text(
                                    text = "Submitting feedback...",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NavyDeep
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Logging your request to our exam updates sheet...",
                                    fontSize = 12.sp,
                                    color = SlateTextSecondary
                                )
                            }
                        }

                        FeedbackDialogState.SUCCESS -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFECFDF5)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Success",
                                        tint = EmeraldSuccess,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Feedback Received!",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyDeep
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Thank you for helping improve ValidPic! We regularly review exam requests and update official portal specifications.",
                                    fontSize = 13.sp,
                                    color = SlateTextSecondary,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Button(
                                    onClick = onDismiss,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = NavyDeep)
                                ) {
                                    Text(
                                        text = "Done",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        FeedbackDialogState.FALLBACK -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = AmberWarning,
                                    modifier = Modifier.size(36.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "Offline / Direct Send Ready",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyDeep
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "We couldn't connect to online sync right now. You can send your feedback directly with 1 tap via email or copy it:",
                                    fontSize = 12.sp,
                                    color = SlateTextSecondary,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        sendViaEmailIntent(context, selectedCategory.title, fullFeedbackBody)
                                        onDismiss()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = NavyDeep)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Send via Email", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedButton(
                                    onClick = {
                                        copyToClipboard(context, fullFeedbackBody)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Copy Text to Clipboard", fontSize = 13.sp)
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                TextButton(onClick = { dialogState = FeedbackDialogState.INPUT }) {
                                    Text(text = "Back to Edit", fontSize = 12.sp, color = SlateTextSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun sendViaEmailIntent(context: Context, categoryTitle: String, fullBody: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:support@validpic.app")
        putExtra(Intent.EXTRA_SUBJECT, "[ValidPic Feedback] $categoryTitle")
        putExtra(Intent.EXTRA_TEXT, fullBody)
    }
    try {
        context.startActivity(Intent.createChooser(intent, "Send feedback via..."))
    } catch (e: Exception) {
        copyToClipboard(context, fullBody)
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("ValidPic Feedback", text))
    Toast.makeText(context, "Feedback copied to clipboard!", Toast.LENGTH_SHORT).show()
}

@Composable
private fun CategoryChip(
    category: FeedbackCategory,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) BlueAccent else SlateBorder
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = category.icon, fontSize = 13.sp)
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = category.title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) BlueAccent else SlateTextPrimary
            )
        }
    }
}

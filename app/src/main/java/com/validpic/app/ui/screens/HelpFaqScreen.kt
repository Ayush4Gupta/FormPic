package com.validpic.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.validpic.app.R
import com.validpic.app.ui.components.FeedbackDialog
import com.validpic.app.ui.components.ValidPicTopAppBar
import com.validpic.app.ui.theme.NavyDeep
import com.validpic.app.ui.theme.SlateBorder
import com.validpic.app.ui.theme.SlateTextPrimary
import com.validpic.app.ui.theme.SlateTextSecondary

data class FaqItem(val questionRes: Int, val answerRes: Int)

@Composable
fun HelpFaqScreen(onNavigateBack: () -> Unit) {
    var showFeedbackDialog by remember { mutableStateOf(false) }

    if (showFeedbackDialog) {
        FeedbackDialog(onDismiss = { showFeedbackDialog = false })
    }

    val faqList = remember {
        listOf(
            FaqItem(R.string.faq_q1, R.string.faq_a1),
            FaqItem(R.string.faq_q2, R.string.faq_a2),
            FaqItem(R.string.faq_q3, R.string.faq_a3),
            FaqItem(R.string.faq_q4, R.string.faq_a4),
            FaqItem(R.string.faq_q5, R.string.faq_a5),
            FaqItem(R.string.faq_q6, R.string.faq_a6)
        )
    }

    Scaffold(
        topBar = {
            ValidPicTopAppBar(
                title = stringResource(R.string.faq_title),
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(faqList.size) { index ->
                FaqCard(faqItem = faqList[index])
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Can't find your exam or need help?",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDeep
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Request official specifications for State PSCs, Police, or Universities directly from our team.",
                            fontSize = 12.sp,
                            color = SlateTextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { showFeedbackDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = NavyDeep),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Send Feedback / Request Preset",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun FaqCard(faqItem: FaqItem) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(faqItem.questionRes),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SlateTextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = NavyDeep
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(faqItem.answerRes),
                        fontSize = 13.sp,
                        color = SlateTextSecondary,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

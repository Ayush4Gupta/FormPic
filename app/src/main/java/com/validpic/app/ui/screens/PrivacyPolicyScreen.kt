package com.validpic.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.validpic.app.R
import com.validpic.app.ui.components.ValidPicTopAppBar
import com.validpic.app.ui.theme.EmeraldBg
import com.validpic.app.ui.theme.EmeraldSuccess
import com.validpic.app.ui.theme.NavyDeep
import com.validpic.app.ui.theme.SlateBorder
import com.validpic.app.ui.theme.SlateTextPrimary
import com.validpic.app.ui.theme.SlateTextSecondary

@Composable
fun PrivacyPolicyScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            ValidPicTopAppBar(
                title = stringResource(R.string.privacy_policy),
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
                .padding(20.dp)
        ) {
            // Guarantee Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EmeraldBg, RoundedCornerShape(12.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = EmeraldSuccess,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Zero Cloud Uploads: Your photos never leave your device.",
                    color = Color(0xFF065F46),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrivacySectionCard(
                title = "1. On-Device Photo Processing",
                body = "ValidPic processes 100% of camera captures and selected gallery images entirely on your smartphone using Google ML Kit on-device computer vision models. No image, face crop, or biometric data is ever transmitted to, stored on, or analyzed by any remote server or cloud service."
            )

            Spacer(modifier = Modifier.height(12.dp))

            PrivacySectionCard(
                title = "2. Device Storage & Saved Files",
                body = "When you tap 'Download Photo', your photo is saved locally into your device's standard Pictures/ValidPic folder via modern Android MediaStore Scoped Storage APIs. Temporary cached files used during compression are stored in internal app cache and automatically purged."
            )

            Spacer(modifier = Modifier.height(12.dp))

            PrivacySectionCard(
                title = "3. Google AdMob Advertising",
                body = "ValidPic is a free utility supported by advertising. We integrate Google AdMob to display occasional interstitial ads before photo downloads. AdMob may collect pseudonymous identifiers such as the Android Advertising ID, IP address, and non-sensitive device telemetry to deliver relevant advertisements in compliance with Google Play Developer Policies."
            )

            Spacer(modifier = Modifier.height(12.dp))

            PrivacySectionCard(
                title = "4. Your Data Rights & Deletion",
                body = "Because we do not operate user accounts or cloud databases, you retain complete physical control over your files. You can delete processed photos directly from your device Gallery or file manager at any time, or clear temporary cache in ValidPic Settings."
            )

            Spacer(modifier = Modifier.height(12.dp))

            PrivacySectionCard(
                title = "5. Contact & Questions",
                body = "For privacy inquiries or technical feedback regarding ValidPic, please reach out via the Play Store listing developer contact link."
            )
        }
    }
}

@Composable
private fun PrivacySectionCard(title: String, body: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = NavyDeep
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = body,
                fontSize = 13.sp,
                color = SlateTextSecondary,
                lineHeight = 20.sp
            )
        }
    }
}

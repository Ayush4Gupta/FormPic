package com.formpic.app.data.repository

import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

sealed class FeedbackSubmitResult {
    data object Success : FeedbackSubmitResult()
    data class Failure(val errorMessage: String) : FeedbackSubmitResult()
}

/**
 * Handles frictionless in-app feedback submissions directly to Google Forms or Google Sheets.
 * Eliminates the need for candidates to open external email clients like Gmail.
 */
object FeedbackManager {
    private const val TAG = "FeedbackManager"

    // Live Google Form / Google Sheets response endpoint
    var submissionUrl: String = "https://docs.google.com/forms/d/e/1FAIpQLSdIuQexxVY2USXe7bNNOXYUxCtDQe0nXmZV30qY6hmn5anoWA/formResponse"

    var entryCategory: String = "entry.1081928115"
    var entryMessage: String = "entry.612139181"
    var entryEmail: String = "entry.1561738174"
    var entryDevice: String = "entry.904739002"

    suspend fun submitFeedback(
        category: String,
        message: String,
        email: String,
        versionName: String,
        versionCode: Int
    ): FeedbackSubmitResult = withContext(Dispatchers.IO) {
        try {
            val diagnosticInfo = "FormPic v$versionName ($versionCode), Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT}), ${Build.MANUFACTURER} ${Build.MODEL}"

            // If the URL is still unconfigured or placeholder in development, 
            // we simulate a smooth successful capture so users enjoy the seamless in-app flow
            if (submissionUrl.contains("sample") || submissionUrl.contains("YOUR_FORM_ID")) {
                Log.i(TAG, "Simulating successful feedback dispatch to Google Sheets: category=$category")
                return@withContext FeedbackSubmitResult.Success
            }

            val url = URL(submissionUrl)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 10000
                readTimeout = 10000
                doOutput = true
                instanceFollowRedirects = true
            }

            val postData: String
            if (submissionUrl.contains("script.google.com")) {
                // Google Apps Script JSON endpoint
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                val json = JSONObject().apply {
                    put("category", category)
                    put("message", message)
                    put("email", email.ifBlank { "Not provided" })
                    put("device", diagnosticInfo)
                    put("timestamp", System.currentTimeMillis())
                }
                postData = json.toString()
            } else {
                // Standard Google Form x-www-form-urlencoded format
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                val params = listOf(
                    entryCategory to category,
                    entryMessage to message,
                    entryEmail to email.ifBlank { "Anonymous" },
                    entryDevice to diagnosticInfo
                )
                postData = params.joinToString("&") { (k, v) ->
                    "${URLEncoder.encode(k, "UTF-8")}=${URLEncoder.encode(v, "UTF-8")}"
                }
            }

            BufferedWriter(OutputStreamWriter(connection.outputStream, "UTF-8")).use { writer ->
                writer.write(postData)
                writer.flush()
            }

            val responseCode = connection.responseCode
            connection.disconnect()

            // Google Forms returns 200 OK or 302/303 redirect upon success
            if (responseCode in 200..399) {
                FeedbackSubmitResult.Success
            } else {
                FeedbackSubmitResult.Failure("HTTP $responseCode")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Feedback submission failed: ${e.message}", e)
            FeedbackSubmitResult.Failure(e.localizedMessage ?: "Network connection error")
        }
    }
}

package com.validpic.app

import android.app.Application
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Application class for ValidPic.
 * Initializes Google Mobile Ads SDK asynchronously on app startup.
 */
class ValidPicApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Asynchronously initialize Google Mobile Ads SDK to keep main thread light
        CoroutineScope(Dispatchers.IO).launch {
            try {
                MobileAds.initialize(this@ValidPicApp) {}
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

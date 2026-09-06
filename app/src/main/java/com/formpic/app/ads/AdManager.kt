package com.formpic.app.ads

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * Robust, policy-compliant AdMob Interstitial Manager.
 * Designed with a strict non-blocking policy: If ads fail, are loading, or the device is offline,
 * the user's download action completes immediately without delay.
 */
object AdManager {

    // Official Google sample Interstitial Ad Unit ID for safe debug testing
    private const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

    // Official Production Interstitial Ad Unit ID for FormPic
    private const val PROD_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-2773192018816474/6540310380"

    private val activeAdUnitId: String
        get() = if (com.formpic.app.BuildConfig.DEBUG) {
            TEST_INTERSTITIAL_AD_UNIT_ID
        } else {
            PROD_INTERSTITIAL_AD_UNIT_ID
        }

    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false
    private var lastAdShowTimeMs = 0L
    private const val MIN_AD_INTERVAL_MS = 60_000L // Frequency cap: minimum 60s between ads

    /**
     * Preloads an interstitial ad in the background.
     */
    fun preloadInterstitial(context: Context) {
        if (interstitialAd != null || isLoading) return
        if (!isNetworkAvailable(context)) return

        isLoading = true
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            activeAdUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoading = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isLoading = false
                }
            }
        )
    }

    /**
     * Shows interstitial ad before download with 100% resilient fallback.
     * [onProceed] is ALWAYS guaranteed to be invoked.
     */
    fun showDownloadInterstitial(activity: Activity, onProceed: () -> Unit) {
        val now = System.currentTimeMillis()
        val ad = interstitialAd

        // Check if ad is available and respects frequency capping
        if (ad != null && (now - lastAdShowTimeMs >= MIN_AD_INTERVAL_MS)) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    lastAdShowTimeMs = System.currentTimeMillis()
                    preloadInterstitial(activity.applicationContext)
                    onProceed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    preloadInterstitial(activity.applicationContext)
                    onProceed()
                }

                override fun onAdShowedFullScreenContent() {
                    interstitialAd = null
                }
            }

            ad.show(activity)
        } else {
            // Immediate fallback: Proceed with download without blocking user
            if (ad == null) {
                preloadInterstitial(activity.applicationContext)
            }
            onProceed()
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

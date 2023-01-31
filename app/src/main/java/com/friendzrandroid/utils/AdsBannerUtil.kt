package com.friendzrandroid.utils

import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

object AdsBannerUtil {

    private const val TAG = "AdsBannerUtil"

    fun loadAds(bannerAdView: AdView) {

        val adRequest = AdRequest.Builder().build()
        bannerAdView.loadAd(adRequest)

        bannerAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {

//                Log.e(TAG, "onAdLoaded: isTestDevice ${adRequest.isTestDevice(requireContext())}")

                Log.e(
                    TAG,
                    "onAdLoaded -> ${bannerAdView.responseInfo?.mediationAdapterClassName}"
                )
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                Log.e(TAG, "onAdFailedToLoad: ${p0.message}")
            }
        }

    }
}
package com.atech.theme

import android.util.Log
import com.atech.theme.databinding.RowAdsViewBinding

private const val TAG = "ads"

enum class AdsUnit(val id: String) {
    Attendance("ca-app-pub-6172727030505608/6951909514"), EventAndNoticeDes("ca-app-pub-6172727030505608/3091721400"), Holiday(
        "ca-app-pub-6172727030505608/4734602419"
    ),
    Home("ca-app-pub-6172727030505608/5014774210"), Notice("ca-app-pub-6172727030505608/7285714339"), Syllabus(
        "ca-app-pub-6172727030505608/3976092502"
    ),
    Miscellaneous("ca-app-pub-6172727030505608/8130379370")
}

//fun initAds(mContext: Context) {
//    MobileAds.initialize(mContext) {}
//    if (BuildConfig.DEBUG) {
//        val testDeviceIds = listOf("CE65A74AF824CCB43EEE62129E5A103E")
//        val requestConfiguration = RequestConfiguration.Builder()
//            .setTestDeviceIds(testDeviceIds)
//            .build()
//        MobileAds.setRequestConfiguration(requestConfiguration)
//    }
//}

fun RowAdsViewBinding.setAdsUnit(id: AdsUnit = AdsUnit.Miscellaneous) {
    Log.d(TAG, "setAdsUnit: $id")
    root.visibility = android.view.View.GONE
//    val adView = AdView(root.context)
//    adView.setAdSize(AdSize.BANNER)
//    adView.adUnitId = id.id
//
//    adView.loadAd(AdRequest.Builder().build())
//    adView.adListener = object : com.google.android.gms.ads.AdListener() {
//        override fun onAdLoaded() {
//            super.onAdLoaded()
//            root.visibility = android.view.View.VISIBLE
//        }
//
//        override fun onAdFailedToLoad(p0: LoadAdError) {
//            super.onAdFailedToLoad(p0)
//            root.visibility = android.view.View.GONE
//            Log.d(TAG, "onAdFailedToLoad: ${p0.message}")
//        }
//    }
//    root.childCount.let {
//        if (it == 0)
//            root.addView(adView)
//    }
}
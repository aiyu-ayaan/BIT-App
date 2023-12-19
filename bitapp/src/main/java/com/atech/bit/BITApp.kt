package com.atech.bit

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.sanghun.compose.video.cache.VideoPlayerCacheManager

@HiltAndroidApp
class BITApp : Application() {
    override fun onCreate() {
        super.onCreate()
        VideoPlayerCacheManager.initialize(this, 1024 * 1024 * 1024)
    }
}
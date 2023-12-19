package com.atech.bit

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import dagger.hilt.android.HiltAndroidApp
import io.sanghun.compose.video.cache.VideoPlayerCacheManager

@HiltAndroidApp
class BITApp : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        VideoPlayerCacheManager.initialize(this, 1024 * 1024 * 1024)

    }

    override fun newImageLoader(): ImageLoader =
        ImageLoader(this).newBuilder()
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.05)
                    .strongReferencesEnabled(true)
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache {
                DiskCache.Builder()
                    .maxSizePercent(0.03)
                    .directory(cacheDir)
                    .build()
            }.logger(DebugLogger())
            .build()

}
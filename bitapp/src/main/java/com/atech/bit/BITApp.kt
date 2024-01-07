package com.atech.bit

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import com.atech.bit.ui.activity.main.MainViewModel
import com.atech.bit.utils.mapWithNotificationEnable
import com.atech.bit.utils.mapWithNotificationEnableTest
import com.atech.chat.utils.getChatSetting
import com.atech.core.usecase.AutoDeleteChatIn30Days
import com.atech.core.utils.BitAppScope
import com.atech.core.utils.CHANNEL_APP
import com.atech.core.utils.CHANNEL_DES
import com.atech.core.utils.CHANNEL_EVENT
import com.atech.core.utils.CHANNEL_ID_APP
import com.atech.core.utils.CHANNEL_ID_EVENT
import com.atech.core.utils.CHANNEL_ID_NOTICE
import com.atech.core.utils.CHANNEL_ID_UPDATE
import com.atech.core.utils.CHANNEL_NOTICE
import com.atech.core.utils.CHANNEL_UPDATE
import com.atech.core.utils.MAX_APP_OPEN_TIME
import com.atech.core.utils.SharePrefKeys
import com.atech.core.utils.fromJSON
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import io.sanghun.compose.video.cache.VideoPlayerCacheManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class BITApp : Application(), ImageLoaderFactory {

    @Inject
    lateinit var pref: SharedPreferences


    @Inject
    lateinit var fcm: FirebaseMessaging

    @Inject
    lateinit var autoDeleteChatIn30Days: AutoDeleteChatIn30Days

    @BitAppScope
    @Inject
    lateinit var bitAppScope: CoroutineScope

    private val appNotificationSetting: MainViewModel.AppNotificationEnable
        get() = fromJSON(
            pref.getString(
                SharePrefKeys.AppNotificationSettings.name, ""
            )!!, MainViewModel.AppNotificationEnable::class.java
        ) ?: MainViewModel.AppNotificationEnable()

    override fun onCreate() {
        super.onCreate()
        // video cache
        VideoPlayerCacheManager.initialize(this, 1024 * 1024 * 1024)

//        setting up notification channels
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNoticeNotificationChannel()
            createEventNotificationChannel()
            createUpdateNotificationChannel()
            createAppNotificationChannel()
        }
        setUpFcm()
        autoDeleteLogic()
    }

    private fun autoDeleteLogic() {
        if (getChatSetting(pref).isAutoDeleteChat) bitAppScope.launch {
            autoDeleteChatIn30Days.invoke()
        }
    }

    private fun setUpFcm() {
        if (BuildConfig.DEBUG)
            mapWithNotificationEnableTest(
                appNotificationSetting
            ).forEach(::notificationSetting)
        else mapWithNotificationEnable(appNotificationSetting).forEach(::notificationSetting)
    }

    private fun notificationSetting(
        pair: Pair<String, Boolean>
    ) = this.apply {
        if (pair.second) fcm.subscribeToTopic(pair.first)
        else fcm.unsubscribeFromTopic(pair.first)
    }

    private fun getValue(key: String) = pref.getBoolean(key, true)

    private fun setMaxTimeOpen() {
        val isSetUpDone = pref.getBoolean(SharePrefKeys.SetUpDone.name, false)
        val currentTime = pref.getInt(SharePrefKeys.KeyAppOpenMinimumTime.name, 0)
        if (isSetUpDone) if (currentTime <= MAX_APP_OPEN_TIME) pref.edit()
            .putInt(SharePrefKeys.KeyAppOpenMinimumTime.name, currentTime + 1).apply()
    }


    override fun newImageLoader(): ImageLoader =
        ImageLoader(this).newBuilder().memoryCachePolicy(CachePolicy.ENABLED).memoryCache {
            MemoryCache.Builder(this).maxSizePercent(0.05).strongReferencesEnabled(true).build()
        }.diskCachePolicy(CachePolicy.ENABLED).diskCache {
            DiskCache.Builder().maxSizePercent(0.03).directory(cacheDir).build()
        }.logger(DebugLogger()).build()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNoticeNotificationChannel() {
        val noticeChannel = NotificationChannel(
            CHANNEL_ID_NOTICE, CHANNEL_NOTICE, NotificationManager.IMPORTANCE_HIGH
        )
        noticeChannel.description = CHANNEL_DES
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(noticeChannel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createEventNotificationChannel() {
        val noticeChannel = NotificationChannel(
            CHANNEL_ID_EVENT, CHANNEL_EVENT, NotificationManager.IMPORTANCE_HIGH
        )
        noticeChannel.description = CHANNEL_DES
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(noticeChannel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createUpdateNotificationChannel() {
        val noticeChannel = NotificationChannel(
            CHANNEL_ID_UPDATE, CHANNEL_UPDATE, NotificationManager.IMPORTANCE_HIGH
        )
        noticeChannel.description = CHANNEL_DES
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(noticeChannel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createAppNotificationChannel() {
        val noticeChannel = NotificationChannel(
            CHANNEL_ID_APP, CHANNEL_APP, NotificationManager.IMPORTANCE_HIGH
        )
        noticeChannel.description = CHANNEL_DES
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(noticeChannel)
    }
}
package com.atech.bit

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import com.atech.core.utils.AppTheme
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
import com.atech.theme.setAppTheme
import com.google.android.material.color.DynamicColors
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BITApp : Application() {

    @Inject
    lateinit var pref: SharedPreferences

    @Inject
    lateinit var fcm: FirebaseMessaging
    override fun onCreate() {
        super.onCreate()
        setDynamicTheme()
        when (pref.getString(SharePrefKeys.AppTheme.name, AppTheme.Sys.name)) {
            AppTheme.Sys.name -> setAppTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            AppTheme.Light.name -> setAppTheme(AppCompatDelegate.MODE_NIGHT_NO)
            AppTheme.Dark.name -> setAppTheme(AppCompatDelegate.MODE_NIGHT_YES)
        }
        setUpFcm()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNoticeNotificationChannel()
            createEventNotificationChannel()
            createUpdateNotificationChannel()
            createAppNotificationChannel()
        }
        setMaxTimeOpen()
    }

    private fun setDynamicTheme() {
        pref.getBoolean(SharePrefKeys.IsDynamicThemeEnabled.name, true).let {
            if (it) DynamicColors.applyToActivitiesIfAvailable(this)
        }
    }

    private fun setMaxTimeOpen() {
        val isSetUpDone = pref.getBoolean(SharePrefKeys.SetUpDone.name, false)
        val currentTime = pref.getInt(SharePrefKeys.KeyAppOpenMinimumTime.name, 0)
        if (isSetUpDone)
            if (currentTime <= MAX_APP_OPEN_TIME)
                pref.edit().putInt(SharePrefKeys.KeyAppOpenMinimumTime.name, currentTime + 1)
                    .apply()
    }

    private fun setUpFcm() {
        fcm.subscribeToTopic(resources.getString(com.atech.theme.R.string.topic_update))
        if(getValue(SharePrefKeys.IsEnableNoticeNotification.name))
            fcm.subscribeToTopic(resources.getString(com.atech.theme.R.string.topic_notice))
        else
            fcm.unsubscribeFromTopic(resources.getString(com.atech.theme.R.string.topic_notice))

        if(getValue(SharePrefKeys.IsEnableEventNotification.name))
            fcm.subscribeToTopic(resources.getString(com.atech.theme.R.string.topic_event))
        else
            fcm.unsubscribeFromTopic(resources.getString(com.atech.theme.R.string.topic_event))

        if(getValue(SharePrefKeys.IsEnableAppNotification.name))
            fcm.subscribeToTopic(resources.getString(com.atech.theme.R.string.topic_app))
        else
            fcm.unsubscribeFromTopic(resources.getString(com.atech.theme.R.string.topic_app))
    }

    private fun getValue(key: String) = pref.getBoolean(key, true)

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNoticeNotificationChannel() {
        val noticeChannel = NotificationChannel(
            CHANNEL_ID_NOTICE,
            CHANNEL_NOTICE,
            NotificationManager.IMPORTANCE_HIGH
        )
        noticeChannel.description = CHANNEL_DES
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(noticeChannel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createEventNotificationChannel() {
        val noticeChannel = NotificationChannel(
            CHANNEL_ID_EVENT,
            CHANNEL_EVENT,
            NotificationManager.IMPORTANCE_HIGH
        )
        noticeChannel.description = CHANNEL_DES
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(noticeChannel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createUpdateNotificationChannel() {
        val noticeChannel = NotificationChannel(
            CHANNEL_ID_UPDATE,
            CHANNEL_UPDATE,
            NotificationManager.IMPORTANCE_HIGH
        )
        noticeChannel.description = CHANNEL_DES
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(noticeChannel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createAppNotificationChannel() {
        val noticeChannel = NotificationChannel(
            CHANNEL_ID_APP,
            CHANNEL_APP,
            NotificationManager.IMPORTANCE_HIGH
        )
        noticeChannel.description = CHANNEL_DES
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(noticeChannel)
    }
}
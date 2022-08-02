package com.atech.bit

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import com.atech.core.utils.*
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
        DynamicColors.applyToActivitiesIfAvailable(this)

        when (pref.getString(KEY_APP_THEME, AppTheme.Sys.name)) {
            AppTheme.Sys.name -> setAppTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            AppTheme.Light.name -> setAppTheme(AppCompatDelegate.MODE_NIGHT_NO)
            AppTheme.Dark.name -> setAppTheme(AppCompatDelegate.MODE_NIGHT_YES)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNoticeNotificationChannel()
            createEventNotificationChannel()
            createUpdateNotificationChannel()
            createAppNotificationChannel()
        }
        setUpFcm()
    }

    private fun setUpFcm() {
        fcm.subscribeToTopic(resources.getString(R.string.topic_notice))
        fcm.subscribeToTopic(resources.getString(R.string.topic_event))
        fcm.subscribeToTopic(resources.getString(R.string.topic_app))
        fcm.subscribeToTopic(resources.getString(R.string.topic_update))
    }

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
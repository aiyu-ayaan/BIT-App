package com.atech.bit

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
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


    @Inject
    lateinit var remoteConfigUtil: RemoteConfigUtil
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
        setButtonVisibility()
        setPosterVisibility()
        setMaxTimeOpen()
    }

    private fun setMaxTimeOpen() {
        val currentTime = pref.getInt(KEY_APP_OPEN_MINIMUM_TIME, 0)
        if (currentTime <= MAX_APP_OPEN_TIME)
            pref.edit().putInt(KEY_APP_OPEN_MINIMUM_TIME, currentTime + 1).apply()
    }

    private fun setPosterVisibility() {
        val times = pref.getInt(KEY_HOME_NOTICE_ANNOUNCEMENT_CARD_VIEW, 1)
        pref.edit().putInt(KEY_HOME_NOTICE_ANNOUNCEMENT_CARD_VIEW, times + 1)
            .apply()
    }

    private fun setButtonVisibility() {
        remoteConfigUtil.fetchData({
            Log.d(TAG_REMOTE, "setButtonVisibility: ${it.message}")
        }) {
            val syllabusVisibilityJson = it.getString(
                KEY_SYLLABUS_VISIBILITY
            )
            pref.edit()
                .putString(
                    KEY_SYLLABUS_VISIBILITY_PREF_CONFIG,
                    syllabusVisibilityJson
                ).apply()
        }
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
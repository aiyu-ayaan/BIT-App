/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 3/22/22, 10:45 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 3/22/22, 10:27 AM
 */



package com.atech.bit.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.atech.bit.R
import com.atech.bit.ui.activity.main_activity.MainActivity
import com.atech.bit.ui.fragments.event.event_description.EventDescriptionFragmentArgs
import com.atech.bit.ui.fragments.notice.description.NoticeDetailFragmentArgs
import com.atech.core.utils.*
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

/**
 * Service class for push notification.
 * @author Ayaan
 * @since 2.0
 */
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FcmService : FirebaseMessagingService() {

    private val TAG = "FcmService"
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        val id = when {
            p0.data["type"] == "Notice" -> CHANNEL_ID_NOTICE
            p0.data["type"] == "Event" -> CHANNEL_ID_EVENT
            p0.data["type"] == "Update" -> CHANNEL_ID_UPDATE
            else -> CHANNEL_ID_APP
        }
        createNotice(p0, id)
    }

    private fun createNotice(p0: RemoteMessage, id: String) {
        val builder = NotificationCompat.Builder(this, id)
            .setSmallIcon(R.drawable.bitnotice)
            .setContentTitle(p0.data["type"])
            .setContentText(p0.data["title"])
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val pendingIntent = when {
            p0.data["type"] == "Notice" -> providePendingIntentNotice(getPath(p0))
            p0.data["type"] == "Event" -> providePendingIntentEvent(getPath(p0))
            p0.data["type"] == "Update" -> providePendingIntentUpdate()
            else -> providePendingIntentApp()
        }
        val link = p0.data["imageLink"].toString()
        if (link.isNotBlank()) {
            link.applyImageUrl(builder)
        }
        builder.setContentIntent(pendingIntent)
        val managerCompat = NotificationManagerCompat.from(this)
        managerCompat.notify(Random.nextInt(), builder.build())
    }


    @SuppressLint("UnspecifiedImmutableFlag")
    private fun providePendingIntentEvent(path: String) =
        NavDeepLinkBuilder(this)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.eventDescriptionFragment)
            .setArguments(EventDescriptionFragmentArgs(path).toBundle())
            .createPendingIntent()

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun providePendingIntentApp() =
        PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            getPendingIntentFlag()
        )

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun providePendingIntentUpdate() =
        PendingIntent.getActivity(
            this,
            0,
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            ).also {
                it.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            getPendingIntentFlag()
        )


    private fun getPath(p: RemoteMessage): String =
        p.data["path"].toString().trim()


    private fun providePendingIntentNotice(path: String) =
        NavDeepLinkBuilder(this)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.noticeDetailFragment)
            .setArguments(NoticeDetailFragmentArgs(path).toBundle())
            .createPendingIntent()
}
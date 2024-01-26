/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.message

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import com.atech.bit.R
import com.atech.bit.ui.activity.main.MainActivity
import com.atech.bit.ui.navigation.DeepLinkRoutes
import com.atech.bit.utils.applyImageUrl
import com.atech.core.utils.CHANNEL_ID_APP
import com.atech.core.utils.CHANNEL_ID_EVENT
import com.atech.core.utils.CHANNEL_ID_NOTICE
import com.atech.core.utils.CHANNEL_ID_UPDATE
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

private const val TAG = "MessageService"

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MessageService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val id = when {
            message.data["type"] == "Notice" -> CHANNEL_ID_NOTICE
            message.data["type"] == "Event" -> CHANNEL_ID_EVENT
            message.data["type"] == "Update" -> CHANNEL_ID_UPDATE
            else -> CHANNEL_ID_APP
        }
        createNotice(message, id)
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
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "createNotice: Permission not granted")
            return
        }
        managerCompat.notify(Random.nextInt(), builder.build())
    }

    private fun providePendingIntentNotice(path: String): PendingIntent? {
        val deepLinkIntent =
            Intent(
                Intent.ACTION_VIEW,
                DeepLinkRoutes.NoticeDetailScreen(path).route.toUri(),
                this,
                MainActivity::class.java
            )
        val deepLinkPendingIntent: PendingIntent? = TaskStackBuilder
            .create(this).run {
                addNextIntentWithParentStack(deepLinkIntent)
                getPendingIntent(0, getPendingIntentFlag())
            }
        return deepLinkPendingIntent
    }

    private fun providePendingIntentEvent(path: String): PendingIntent? {
        val deepLinkIntent =
            Intent(
                Intent.ACTION_VIEW,
                DeepLinkRoutes.EventDetailScreen(path).route.toUri(),
                this,
                MainActivity::class.java
            )
        val deepLinkPendingIntent: PendingIntent? = TaskStackBuilder
            .create(this).run {
                addNextIntentWithParentStack(deepLinkIntent)
                getPendingIntent(0, getPendingIntentFlag())
            }
        return deepLinkPendingIntent
    }

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

    private fun getPendingIntentFlag() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_ONE_SHOT
}
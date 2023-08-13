package com.atech.bit.utils

import com.atech.core.firebase.firestore.NoticeModel

fun NoticeModel.getImageLinkNotification(): String = when (this.sender) {
    "App Notice" -> "https://firebasestorage.googleapis.com/v0/b/theaiyubit.appspot.com/o/Utils%2Fapp_notification.png?alt=media&token=0a7babfe-bf59-4d19-8fc0-98d7fde151a6"
    else -> "https://firebasestorage.googleapis.com/v0/b/theaiyubit.appspot.com/o/Utils%2Fcollege_notifications.png?alt=media&token=c5bbfda0-c73d-4af1-9c3c-cb29a99d126b"
}

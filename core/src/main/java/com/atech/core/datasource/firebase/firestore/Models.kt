/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.core.datasource.firebase.firestore

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class EventModel(
    val created: Long? = null,
    val title: String? = null,
    var content: String? = null,
    val insta_link: String? = null,
    val logo_link: String? = null,
    val path: String? = null,
    val society: String? = null,
    val video_link: String? = null,
    var attach: List<Attach>? = null,
) : Parcelable


@Parcelize
@Keep
data class NoticeModel(
    val title: String? = null,
    val body: String? = null,
    val link: String? = null,
    val sender: String? = null,
    val path: String? = null,
    val created: Long? = null,
) : Parcelable

@Parcelize
@Keep
data class ShareModel(
    val notice: NoticeModel? = null,
    val event: EventModel? = null,
    val attach: List<Attach> = emptyList()
) : Parcelable


@Keep
@Parcelize
data class Attach(
    var added: Long? = null,
    var link: String? = null
) : Parcelable





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

/**
 * Model class to get events from firestore
 */
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


/**
 * Model class to get notices from firestore
 */
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


/**
 * Model class fetch attach files from [NoticeModel] and [EventModel]
 * @see NoticeModel
 * @see EventModel
 */
@Keep
@Parcelize
data class Attach(
    var added: Long? = null,
    var link: String? = null
) : Parcelable





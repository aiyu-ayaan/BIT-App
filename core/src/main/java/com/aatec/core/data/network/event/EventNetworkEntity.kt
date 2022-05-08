/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 3/22/22, 10:45 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 3/22/22, 9:55 AM
 */



package com.aatec.core.data.network.event

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
class EventNetworkEntity : Parcelable {
    var created: Long = 0L
    var event_body: String = ""
    var event_title: String = ""
    var ins_link: String = ""
    var logo_link: String = ""
    var society: String = ""
    var web_link: String = ""
    var poster_link: String = ""
    val path: String = ""
}
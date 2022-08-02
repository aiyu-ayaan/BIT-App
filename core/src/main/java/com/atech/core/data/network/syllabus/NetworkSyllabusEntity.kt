/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 2/21/22, 10:27 AM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 2/21/22, 10:18 AM
 */



package com.atech.core.data.network.syllabus

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
class NetworkSyllabusEntity : Parcelable {

    val subject: String = ""
    val code: String = ""
    val credits: Int = 1
    val openCode: String = ""
    val type: String = ""
    val shortName: String = ""
    val listOrder: Int = 1
    val fromNetwork: Boolean = true
    val content: String = ""
    val book: String = ""
    val reference: String = ""
    val deprecated: Boolean = false
}
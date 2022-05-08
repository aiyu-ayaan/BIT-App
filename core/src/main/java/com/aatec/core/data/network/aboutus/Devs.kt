/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 4/25/22, 9:19 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 4/25/22, 2:09 AM
 */



package com.aatec.core.data.network.aboutus

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
class Devs : Parcelable {
    val sno: Int = 0
    val name: String = ""
    val github: String = ""
    val linkedin: String = ""
    val img_link: String = ""
    val facebook: String = ""
    val instagram: String = ""
    val des: String = ""
}
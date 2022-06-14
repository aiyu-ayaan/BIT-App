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
data class Devs(
    val sno: Int? = null,
    val name: String? = null,
    val img_link: String? = null,
    val website: String? = null,
    val stackoverflow: String? = null,
    val github: String? = null,
    val linkedin: String? = null,
    val instagram: String? = null,
    val des: String? = null
) : Parcelable
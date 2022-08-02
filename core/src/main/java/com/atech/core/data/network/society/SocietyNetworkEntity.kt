/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/1/21, 1:09 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/1/21, 11:14 AM
 */



package com.atech.core.data.network.society

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
class SocietyNetworkEntity : Parcelable {
    val name: String = ""
    val des: String = ""
    val ins: String = ""
    val logo_link = ""
}
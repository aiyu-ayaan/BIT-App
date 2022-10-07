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
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
class SocietyNetworkEntity : Parcelable {
    @IgnoredOnParcel
    val name: String = ""
    @IgnoredOnParcel
    val des: String = ""
    @IgnoredOnParcel
    val ins: String = ""
    @IgnoredOnParcel
    val logo_link = ""
}
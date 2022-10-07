/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 2/21/22, 10:27 AM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 2/21/22, 10:18 AM
 */



package com.atech.core.data.network.holiday

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
class HolidayNetworkEntity : Parcelable {
    @IgnoredOnParcel
    var sno: Int = 0

    @IgnoredOnParcel
    var day: String = ""

    @IgnoredOnParcel
    var date: String = ""

    @IgnoredOnParcel
    var occasion: String = ""

    @IgnoredOnParcel
    var type = ""

    @IgnoredOnParcel
    var month = ""
}
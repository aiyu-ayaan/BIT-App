/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 2/21/22, 10:27 AM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 2/21/22, 10:18 AM
 */



package com.aatec.core.data.network.holiday

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
class HolidayNetworkEntity : Parcelable {
    var sno: Int = 0
    var day: String = ""
    var date: String = ""
    var occasion: String = ""
    var type = ""
    var month = ""
}
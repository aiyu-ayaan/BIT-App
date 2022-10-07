package com.atech.core.data.network.user

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import kotlinx.android.parcel.Parcelize

@Keep
@Suppress("")
@Parcelize
data class UserModel(
    var uid: String? = null,
    var name: String? = null,
    var email: String? = null,
    var profilePic: String? = null,
    var syncTime: Long? = System.currentTimeMillis()
) : Parcelable

@Keep
data class AttendanceUploadModel(
    @ColumnInfo(name = "subject_name")
    val subject: String,
    val total: Int,
    val present: Int,
    val teacher: String?,
    val fromSyllabus: Boolean? = false,
    val isArchive : Boolean? = false,
    val created: Long? = System.currentTimeMillis()
)
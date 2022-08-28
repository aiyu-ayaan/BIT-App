package com.atech.core.data.network.user

import androidx.annotation.Keep
import androidx.room.ColumnInfo

@Keep
data class UserModel(
    var uid: String? = null,
    var name: String? = null,
    var email: String? = null,
    var profilePic: String? = null,
)

@Keep
data class AttendanceUploadModel(
    var id: Int = 0,
    @ColumnInfo(name = "subject_name")
    val subject: String,
    val total: Int,
    val present: Int,
    val teacher: String?,
    val fromSyllabus: Boolean? = false,
    val created: Long? = System.currentTimeMillis()
)
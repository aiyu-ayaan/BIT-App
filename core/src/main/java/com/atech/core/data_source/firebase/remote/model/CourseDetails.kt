package com.atech.core.data_source.firebase.remote.model

import androidx.annotation.Keep


@Keep
data class CourseDetails(
    val course: List<CourseDetailModel>
)

@Keep
data class CourseDetailModel(
    val name: String,
    val sem: Int
)
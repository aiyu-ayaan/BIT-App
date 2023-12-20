package com.atech.core.datasource.firebase.remote.model

import androidx.annotation.Keep


val defaultCourseSem = CourseDetails(
    listOf(
        CourseDetailModel("bca",6),
        CourseDetailModel("bba",6),
        CourseDetailModel("mca",4),
        CourseDetailModel("mba",4),
    )
)

@Keep
data class CourseDetails(
    val course: List<CourseDetailModel>
)

@Keep
data class CourseDetailModel(
    val name: String,
    val sem: Int
)

fun CourseDetails.toMap(): Map<String, String> {
    return course.associate { it.name.uppercase() to it.sem.toString() }
}
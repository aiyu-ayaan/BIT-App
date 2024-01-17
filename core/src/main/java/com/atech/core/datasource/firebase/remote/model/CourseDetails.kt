/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.core.datasource.firebase.remote.model

import androidx.annotation.Keep

/**
 * Default course details
 */
val defaultCourseSem = CourseDetails(
    listOf(
        CourseDetailModel("bca", 6),
        CourseDetailModel("bba", 6),
        CourseDetailModel("mca", 4),
        CourseDetailModel("mba", 4),
    )
)

/**
 * Model class for CourseDetails
 * @see CourseDetailModel
 */
@Keep
data class CourseDetails(
    val course: List<CourseDetailModel>
)

/**
 * Model class for fetching course details from RemoteConfig
 * @see CourseDetails
 */
@Keep
data class CourseDetailModel(
    val name: String,
    val sem: Int
)

/**
 * Convert [CourseDetails] to Map
 * @see CourseDetails
 */
fun CourseDetails.toMap(): Map<String, String> {
    return course.associate { it.name.uppercase() to it.sem.toString() }
}
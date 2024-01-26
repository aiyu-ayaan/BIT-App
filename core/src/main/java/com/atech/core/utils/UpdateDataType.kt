/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.core.utils

import com.atech.core.datasource.datastore.Cgpa
import com.atech.core.datasource.firebase.auth.AttendanceUploadModel

sealed interface UpdateDataType {
    data class UploadAttendance(val data: List<AttendanceUploadModel>) : UpdateDataType
    data class UpdateCourseSem(val course: String, val sem: String) : UpdateDataType
    data class UpdateCgpa(val cgpa: Cgpa) : UpdateDataType
}
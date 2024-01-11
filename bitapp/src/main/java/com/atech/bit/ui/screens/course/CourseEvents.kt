/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.course

import com.atech.core.datasource.firebase.remote.model.CourseDetailModel

sealed interface CourseEvents {
    data class NavigateToSemChoose(val model: CourseDetailModel) : CourseEvents

    data class OnSemChange(val sem: Int) : CourseEvents

    data object OnSwitchToggle : CourseEvents

    data class ErrorDuringLoadingError(val message: String) : CourseEvents
}
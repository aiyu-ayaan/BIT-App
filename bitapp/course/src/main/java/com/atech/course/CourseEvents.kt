package com.atech.course

import com.atech.core.data_source.firebase.remote.model.CourseDetailModel

sealed class CourseEvents {
    data class NavigateToSemChoose(val model : CourseDetailModel) : CourseEvents()

    data class OnSemChange(val sem : Int) : CourseEvents()
}
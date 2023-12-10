package com.atech.course

import android.content.SharedPreferences
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.atech.core.firebase.remote.model.CourseDetailModel
import com.atech.core.firebase.remote.model.CourseDetails
import com.atech.core.utils.COURSE_DETAILS
import com.atech.core.utils.SharePrefKeys
import com.atech.core.utils.fromJSON
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor(
    pref: SharedPreferences,
) : ViewModel() {
    private val courseDetailsJson =
        (pref.getString(SharePrefKeys.CourseDetails.name, COURSE_DETAILS) ?: COURSE_DETAILS)

    val courseDetails = fromJSON(courseDetailsJson, CourseDetails::class.java)

    private val _currentClickItem = mutableStateOf(CourseDetailModel("bca", 6))
    val currentClickItem: State<CourseDetailModel> get() = _currentClickItem

    fun onEvent(events: CourseEvents) {
        when (events) {
            is CourseEvents.NavigateToSemChoose -> {
                _currentClickItem.value = events.model
            }
        }
    }
}
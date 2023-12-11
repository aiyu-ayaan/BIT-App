package com.atech.course.screen.sub_view

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.core.use_case.KTorUseCase
import com.atech.core.utils.TAGS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewSubjectViewModel @Inject constructor(
    private val kTorUseCase: KTorUseCase,
    state: SavedStateHandle
) : ViewModel() {
    private val course = state.get<String>("course") ?: ""
    private val courseSem = state.get<String>("courseSem") ?: ""
    private val subject = state.get<String>("subject") ?: ""
    private val isOnline = state.get<Boolean>("isOnline") ?: true

    private val _onlineMdContent = mutableStateOf("")
    val onlineMdContent: State<String> get() = _onlineMdContent

    init {
        if (isOnline) {
            getSubjectMarkdown(course, courseSem, subject)
        }
    }

    private fun getSubjectMarkdown(
        course: String,
        courseSem: String,
        subject: String
    ) = viewModelScope.launch {
        try {
            kTorUseCase.fetchSubjectMarkDown(
                course = course,
                courseSem = courseSem,
                subject = subject
            ).let {
                _onlineMdContent.value = it
            }
        } catch (e: Exception) {
            Log.d(TAGS.BIT_DEBUG.name, "getSubjectMarkdown: ${e.message}")
        }
    }
}
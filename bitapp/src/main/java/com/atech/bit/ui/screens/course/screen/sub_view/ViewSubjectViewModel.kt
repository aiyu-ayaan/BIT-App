package com.atech.bit.ui.screens.course.screen.sub_view

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.bit.ui.navigation.replaceAsteriskWithAmpersand
import com.atech.core.usecase.KTorUseCase
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
    val courseSem = state.get<String>("courseSem") ?: ""
    val subject = state.get<String>("subject")?.replaceAsteriskWithAmpersand() ?: ""
    val isOnline = state.get<Boolean>("isOnline") ?: true

    private val _onlineMdContent = mutableStateOf("")
    val onlineMdContent: State<String> get() = _onlineMdContent

    private val _hasError = mutableStateOf(false to "")
    val hasError: State<Pair<Boolean, String>> get() = _hasError


    init {
        if (isOnline)
            getSubjectMarkdown(course, courseSem, subject)
    }


    fun onEvent(events: ViewSubjectEvents) {
        when (events) {
            is ViewSubjectEvents.OnError -> _hasError.value = true to events.message
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
            onEvent(
                ViewSubjectEvents.OnError(
                    "Can't load online syllabus. Check your internet connection."
                )
            )
        }
    }

    sealed interface ViewSubjectEvents {
        data class OnError(val message: String) : ViewSubjectEvents
    }
}
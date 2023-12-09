package com.atech.bit.ui

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.atech.bit.SharedEvents
import com.atech.core.firebase.remote.RemoteConfigHelper
import com.atech.core.utils.RemoteConfigKeys
import com.atech.core.utils.SharePrefKeys
import com.atech.core.utils.TAGS
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val conf: RemoteConfigHelper,
    private val pref: SharedPreferences,
) : ViewModel() {

    init {
        fetchRemoteConfigDetails()
    }

    private val _isSearchActive = mutableStateOf(false)
    val isSearchActive: State<Boolean> get() = _isSearchActive


    fun onEvent(event: SharedEvents) {
        when (event) {
            SharedEvents.ToggleSearchActive -> _isSearchActive.value = !_isSearchActive.value
        }
    }


    private fun fetchRemoteConfigDetails() {
        conf.fetchData(
            failure = {
                Log.e(TAGS.BIT_REMOTE.name, "fetchRemoteConfigDetails: ${it.message}")
            }
        ) {
            conf.getString(RemoteConfigKeys.CourseDetails.value)
                .let { pref.edit().putString(SharePrefKeys.CourseDetails.name, it).apply() }
        }
    }

}
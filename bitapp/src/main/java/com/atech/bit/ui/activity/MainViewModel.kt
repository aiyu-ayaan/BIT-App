package com.atech.bit.ui.activity

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.atech.core.datasource.firebase.remote.RemoteConfigHelper
import com.atech.core.utils.RemoteConfigKeys
import com.atech.core.utils.SharePrefKeys
import com.atech.core.utils.TAGS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val conf: RemoteConfigHelper,
    private val pref: SharedPreferences,
) : ViewModel() {
    fun fetchRemoteConfigDetails() {
        conf.fetchData(
            failure = {
                Log.e(TAGS.BIT_REMOTE.name, "fetchRemoteConfigDetails: ${it.message}")
            }
        ) {
            conf.getString(RemoteConfigKeys.CourseDetails.value)
                .let { pref.edit().putString(SharePrefKeys.CourseDetails.name, it).apply() }
            conf.getString(RemoteConfigKeys.KeyToggleSyllabusSource.value)
                .let {
                    pref.edit().putString(SharePrefKeys.KeyToggleSyllabusSource.name, it).apply()
                }
        }
    }

    private val _isSearchActive = mutableStateOf(false)
    val isSearchActive: State<Boolean> get() = _isSearchActive

    private val _toggleDrawerState = mutableStateOf(null as DrawerValue?)
    val toggleDrawerState: State<DrawerValue?> get() = _toggleDrawerState


    fun onEvent(event: SharedEvents) {
        when (event) {
            SharedEvents.ToggleSearchActive -> _isSearchActive.value = !_isSearchActive.value
            is SharedEvents.ToggleDrawer -> _toggleDrawerState.value = event.state
        }
    }


    sealed class SharedEvents {
        data object ToggleSearchActive : SharedEvents()

        data class ToggleDrawer( val state : DrawerValue?) : SharedEvents()
    }

}
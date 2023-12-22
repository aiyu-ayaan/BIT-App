package com.atech.bit.ui.activity.main

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.atech.bit.utils.getTheme
import com.atech.bit.utils.saveTheme
import com.atech.core.datasource.firebase.remote.RemoteConfigHelper
import com.atech.core.datasource.firebase.remote.model.CourseDetails
import com.atech.core.datasource.firebase.remote.model.defaultCourseSem
import com.atech.core.usecase.HasLogIn
import com.atech.core.utils.RemoteConfigKeys
import com.atech.core.utils.SharePrefKeys
import com.atech.core.utils.TAGS
import com.atech.core.utils.fromJSON
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val conf: RemoteConfigHelper,
    private val pref: SharedPreferences,
    private val logIn: HasLogIn,
) : ViewModel() {
    private val _courseDetail = mutableStateOf(defaultCourseSem)
    val courseDetail: State<CourseDetails> get() = _courseDetail

    val hasSetUpDone = logIn.invoke() || pref.getBoolean(SharePrefKeys.SetUpDone.name, false)

    fun fetchRemoteConfigDetails() {
        conf.fetchData(
            failure = {
                Log.e(TAGS.BIT_REMOTE.name, "fetchRemoteConfigDetails: ${it.message}")
            }
        ) {
            conf.getString(RemoteConfigKeys.CourseDetails.value)
                .let {
                    _courseDetail.value =
                        fromJSON(it, CourseDetails::class.java) ?: defaultCourseSem
                    pref.edit().putString(SharePrefKeys.CourseDetails.name, it).apply()
                }
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
    private val _themeState = mutableStateOf(getTheme(pref))
    val themeState: State<ThemeState> get() = _themeState


    fun onEvent(event: SharedEvents) {
        when (event) {
            SharedEvents.ToggleSearchActive -> _isSearchActive.value = !_isSearchActive.value
            is SharedEvents.ToggleDrawer -> _toggleDrawerState.value = event.state
        }
    }

    fun onThemeChange(event: ThemeEvent) {
        when (event) {
            is ThemeEvent.ChangeTheme -> {
                _themeState.value = event.state
                saveTheme(_themeState.value, pref)
            }
        }
    }


    sealed class SharedEvents {
        data object ToggleSearchActive : SharedEvents()

        data class ToggleDrawer(val state: DrawerValue?) : SharedEvents()
    }

}
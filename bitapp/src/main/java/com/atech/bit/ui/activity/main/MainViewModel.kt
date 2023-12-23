package com.atech.bit.ui.activity.main

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.bit.utils.getTheme
import com.atech.bit.utils.saveTheme
import com.atech.core.datasource.firebase.auth.UserData
import com.atech.core.datasource.firebase.auth.UserModel
import com.atech.core.datasource.firebase.remote.RemoteConfigHelper
import com.atech.core.datasource.firebase.remote.model.CourseDetails
import com.atech.core.datasource.firebase.remote.model.defaultCourseSem
import com.atech.core.usecase.AuthUseCases
import com.atech.core.utils.RemoteConfigKeys
import com.atech.core.utils.SharePrefKeys
import com.atech.core.utils.TAGS
import com.atech.core.utils.fromJSON
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val conf: RemoteConfigHelper,
    private val pref: SharedPreferences,
    private val authUseCases: AuthUseCases
) : ViewModel() {
    private val _courseDetail = mutableStateOf(defaultCourseSem)
    val courseDetail: State<CourseDetails> get() = _courseDetail

    val hasSetUpDone = (authUseCases.hasLogIn.invoke() || pref.getBoolean(
        SharePrefKeys.SetUpDone.name, false
    )) || pref.getBoolean(
        SharePrefKeys.PermanentSkipLogin.name, false
    )
    var action: () -> Unit = {}

    fun checkHasLogIn() = authUseCases.hasLogIn.invoke()

    private val _useModel = mutableStateOf(UserModel())
    val useModel: State<UserModel> get() = _useModel

    private val _userData = mutableStateOf(UserData())
    val userData: State<UserData> get() = _userData

    private val _profileLink = mutableStateOf<String?>(null)
    val profileLink: State<String?> get() = _profileLink

    init {
        if (authUseCases.hasLogIn.invoke()) {
            authUseCases.getUserDataFromAuth().let { (model, _) ->
                if (model != null) {
                    _profileLink.value = model.profilePic
                }
            }
        }
    }


    fun fetchRemoteConfigDetails() {
        conf.fetchData(failure = {
            Log.e(TAGS.BIT_REMOTE.name, "fetchRemoteConfigDetails: ${it.message}")
        }) {
            conf.getString(RemoteConfigKeys.CourseDetails.value).let {
                _courseDetail.value = fromJSON(it, CourseDetails::class.java) ?: defaultCourseSem
                pref.edit().putString(SharePrefKeys.CourseDetails.name, it).apply()
            }
            conf.getString(RemoteConfigKeys.KeyToggleSyllabusSource.value).let {
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
            SharedEvents.PreformSignOut -> {
                authUseCases.signOut.invoke {
                    _useModel.value = UserModel()
                    _userData.value = UserData()
                    _profileLink.value = null
                }
            }

            SharedEvents.FetchUserDetails -> {
                if (authUseCases.hasLogIn.invoke()) {
                    authUseCases.getUserDataFromAuth().let { (model, _) ->
                        if (model != null) {
                            _profileLink.value = model.profilePic
                        }
                    }
                }
            }

            SharedEvents.OpenLogInScreen -> action.invoke()
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

    fun fetchAccountDetail() = viewModelScope.launch {
        authUseCases.getUserSavedData().let {
            it.first?.let { data ->
                _userData.value = data
            }
        }
        authUseCases.getUserFromDatabase().let { (model, _) ->
            if (model != null) {
                _useModel.value = model
            }
        }
    }


    sealed interface SharedEvents {
        data object ToggleSearchActive : SharedEvents
        data class ToggleDrawer(val state: DrawerValue?) : SharedEvents
        data object PreformSignOut : SharedEvents

        data object FetchUserDetails : SharedEvents

        data object OpenLogInScreen : SharedEvents
    }

}
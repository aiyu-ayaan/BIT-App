package com.atech.bit.ui

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
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
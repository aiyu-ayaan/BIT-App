package com.atech.core.utils

import android.util.Log
import com.atech.core.BuildConfig
import com.atech.core.R
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import javax.inject.Inject


class RemoteConfigUtil @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig
) {
    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 60 else 1800
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    }

    fun fetchData(failure: (Exception) -> Unit, success: (FirebaseRemoteConfig) -> Unit) {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG_REMOTE, "Config params updated: ${task.result}")
                    success(remoteConfig)
                } else {
                    failure(task.exception!!)
                }
            }
    }

    fun getBoolean(key: String) = remoteConfig.getBoolean(key)

    fun getString(key: String) = remoteConfig.getString(key)

    fun getLong(key: String) = remoteConfig.getLong(key)
}
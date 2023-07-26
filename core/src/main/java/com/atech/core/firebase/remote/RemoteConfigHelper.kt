package com.atech.core.firebase.remote

import com.atech.core.BuildConfig
import com.atech.core.R
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import javax.inject.Inject

class RemoteConfigHelper @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig
) {
    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else 1800
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    }

    fun fetchData(failure: (Exception) -> Unit, success: FirebaseRemoteConfig.() -> Unit) {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
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
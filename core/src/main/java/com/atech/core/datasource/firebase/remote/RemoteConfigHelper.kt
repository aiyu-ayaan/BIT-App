/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.core.datasource.firebase.remote

import com.atech.core.BuildConfig
import com.atech.core.R
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import javax.inject.Inject

/**
 * Helper class for fetching data from RemoteConfig
 */
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

    /**
     * Fetch data from RemoteConfig
     * @param failure callback for failure
     * @param success callback for success
     */
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

    /**
     * Get Boolean from RemoteConfig
     * @param key key for Boolean
     * @return Boolean
     */

    fun getBoolean(key: String) = remoteConfig.getBoolean(key)

    /**
     * Get String from RemoteConfig
     * @param key key for String
     * @return String
     */
    fun getString(key: String) = remoteConfig.getString(key)

    /**
     * Get Double from RemoteConfig
     * @param key key for Double
     * @return Double
     */
    fun getLong(key: String) = remoteConfig.getLong(key)


}
/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/1/21, 1:09 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/1/21, 11:14 AM
 */



package com.atech.core.utils

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.LiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Class to check weather device is connected to internet or not.
 * @param context Context
 * @return LiveData<Boolean> -if connected return true
 * @author ayaan
 */
class ConnectionManager @Inject constructor(@ApplicationContext val context: Context) :
    LiveData<Boolean>() {
    private lateinit var networkCallbacks: ConnectivityManager.NetworkCallback
    private val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager


    override fun onActive() {
        super.onActive()
        cm.registerDefaultNetworkCallback(connectivityManagerCallback())
    }


    private fun connectivityManagerCallback(): ConnectivityManager.NetworkCallback {
        postValue(false)
        networkCallbacks = object : ConnectivityManager
        .NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                postValue(true)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                postValue(false)

            }
        }
        return networkCallbacks
    }
}
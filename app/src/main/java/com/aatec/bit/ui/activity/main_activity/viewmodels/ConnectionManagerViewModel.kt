/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/1/21, 1:09 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/1/21, 11:14 AM
 */



package com.aatec.bit.ui.activity.main_activity.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.aatec.core.utils.ConnectionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ConnectionManagerViewModel
 *
 * @see com.aatec.bit.utils.ConnectionManager Connection Manager
 */
@HiltViewModel
class ConnectionManagerViewModel @Inject constructor(
    private val connectionManager: ConnectionManager
) : ViewModel() {
    private val _isConnected = connectionManager
    val isConnected: LiveData<Boolean>
        get() = _isConnected

    val isAppReady: Boolean = false
}
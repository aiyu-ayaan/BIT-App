/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.core.utils.connectivity

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun observe() : Flow<Status>

    enum class Status{
        Available, Unavailable, Losing, Lost
    }
}
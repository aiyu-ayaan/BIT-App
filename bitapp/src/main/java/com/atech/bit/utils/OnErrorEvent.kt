/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.utils

sealed interface OnErrorEvent {
    data class OnError(val message: String) : OnErrorEvent
}
package com.aatec.core.utils

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler


val handler = CoroutineExceptionHandler { _, throwable ->
    Log.e(TAG, "Exception : $throwable ")
}
package com.atech.theme

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler

private const val TAG = "CoroutineException"
val handler = CoroutineExceptionHandler { _, throwable ->
    Log.e(TAG, "Exception : $throwable ")
}
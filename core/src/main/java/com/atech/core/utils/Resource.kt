package com.atech.core.utils

sealed class Resource<T>(
    val data: T? = null,
    val error: Exception? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T?) : Resource<T>(data)
    class Error<T>(error: Exception, data: T? = null) : Resource<T>(data = data, error = error)
}

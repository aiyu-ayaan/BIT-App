package com.atech.core.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


inline fun <ResponseObject> networkFetchData(
    crossinline fetch: suspend () -> ResponseObject,
    crossinline action: ((ResponseObject) -> ResponseObject) = { it },
): Flow<DataState<ResponseObject>> = flow {
    emit(DataState.Loading)
    try {
        val data = action(fetch())
        emit(DataState.Success(action(data)))
    } catch (e: Exception) {
        emit(DataState.Error(e))
    }
}.flowOn(handler)


sealed class DataState<out T> {
    data class Success<out T>(val data: T) : DataState<T>()
    data class Error(val exception: Exception) : DataState<Nothing>()
    object Loading : DataState<Nothing>()
    object Empty : DataState<Nothing>()
}

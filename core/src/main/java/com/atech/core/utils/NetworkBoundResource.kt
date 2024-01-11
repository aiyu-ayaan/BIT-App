/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.core.utils

//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.flow
//import kotlinx.coroutines.flow.flowOn
//import retrofit2.HttpException
//import kotlin.coroutines.suspendCoroutine


//inline fun <ResponseObject> networkFetchData(
//    crossinline fetch: suspend () -> ResponseObject,
//    crossinline action: ((ResponseObject) -> ResponseObject) = { it },
//): Flow<DataState<ResponseObject>> = flow {
//    emit(DataState.Loading)
//    try {
//        val data = action(fetch())
//        emit(DataState.Success(action(data)))
//    } catch (e: HttpException) {
//        when (e.code()) {
//            504 -> emit(DataState.Error(NetworkBoundException.NoInternet))
//            404 -> emit(DataState.Error(NetworkBoundException.NotFound))
//            else -> emit(DataState.Error(NetworkBoundException.Unknown(e.code())))
//        }
//    } catch (e: Exception) {
//        emit(DataState.Error(e))
//    }
//}.flowOn(handler)
//
//
//sealed class NetworkBoundException(val code: Int) : Exception() {
//    object NoInternet : NetworkBoundException(504)
//    object NotFound : NetworkBoundException(404)
//    class Unknown(code: Int) : NetworkBoundException(code)
//}
//
//
//sealed class DataState<out T> {
//    data class Success<out T>(val data: T) : DataState<T>()
//    data class Error(val exception: Exception) : DataState<Nothing>()
//    object Loading : DataState<Nothing>()
//    object Empty : DataState<Nothing>()
//}
//
//fun <T> DataState<T>.getData(): T? = when (this) {
//    is DataState.Success -> data
//    else -> null
//}
//
//suspend fun <T> DataState<T>.getDataContinuation(): T? = suspendCoroutine {
//    when (this) {
//        is DataState.Success -> it.resumeWith(Result.success(data))
//        is DataState.Error -> it.resumeWith(Result.failure(exception))
//        else -> Unit
//    }
//}
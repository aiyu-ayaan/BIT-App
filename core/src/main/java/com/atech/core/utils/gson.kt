package com.atech.core.utils

import com.google.gson.Gson

fun <T> fromJSON(json: String, type: Class<T>): T? =
    try {
        Gson().fromJson(json, type)
    } catch (e: Exception) {
        null
    }


fun <T> toJSON(obj: T): String =
    Gson().toJson(obj)

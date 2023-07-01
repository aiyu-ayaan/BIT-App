package com.atech.core.utils

import com.google.gson.Gson

fun <T> fromJSON(json: String, type: Class<T>): T =
    Gson().fromJson(json, type)


fun <T> toJSON(obj: T): String =
    Gson().toJson(obj)

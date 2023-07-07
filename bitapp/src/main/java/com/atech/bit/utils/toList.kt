package com.atech.bit.utils

import androidx.lifecycle.LiveData

fun <T> LiveData<List<T>>.toList(): List<T> {
    return this.value.orEmpty()
}
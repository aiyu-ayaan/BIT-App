package com.atech.utils

fun <T : Any> getSimpleName(clazz: Class<T>): String = clazz.simpleName.orEmpty()

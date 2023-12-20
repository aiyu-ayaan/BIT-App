package com.atech.bit.utils

import com.atech.bit.BuildConfig

fun getVersion() = BuildConfig.VERSION_NAME
    .replace("-beta", "")
    .replace("-playStore", "")
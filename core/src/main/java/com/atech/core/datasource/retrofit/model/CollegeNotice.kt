/*
 *  Created by Aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.core.datasource.retrofit.model

import androidx.annotation.Keep

@Keep
data class CollegeNotice(
    val title: String, val date: String, val link: String
)

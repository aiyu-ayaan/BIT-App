/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.utils

import androidx.annotation.Keep

@Keep
data class SyllabusEnableModel(
    val bca1: Boolean = false,
    val bca2: Boolean = false,
    val bca3: Boolean = false,
    val bca4: Boolean = false,
    val bca5: Boolean = false,
    val bca6: Boolean = false,
    val bba1: Boolean = false,
    val bba2: Boolean = false,
    val bba3: Boolean = false,
    val bba4: Boolean = false,
    val bba5: Boolean = false,
    val bba6: Boolean = false,
    val mca1: Boolean = false,
    val mca2: Boolean = false,
    val mca3: Boolean = false,
    val mca4: Boolean = false,
    val mba1: Boolean = false,
    val mba2: Boolean = false,
    val mba3: Boolean = false,
    val mba4: Boolean = false
)

fun SyllabusEnableModel.compareToCourseSem(courseSem: String) = this.run {
    when (courseSem.lowercase()) {
        "bca1" -> bca1
        "bca2" -> bca2
        "bca3" -> bca3
        "bca4" -> bca4
        "bca5" -> bca5
        "bca6" -> bca6
        "bba1" -> bba1
        "bba2" -> bba2
        "bba3" -> bba3
        "bba4" -> bba4
        "bba5" -> bba5
        "bba6" -> bba6
        "mca1" -> mca1
        "mca2" -> mca2
        "mca3" -> mca3
        "mca4" -> mca4
        "mba1" -> mba1
        "mba2" -> mba2
        "mba3" -> mba3
        "mba4" -> mba4
        else ->
            false
    }
}

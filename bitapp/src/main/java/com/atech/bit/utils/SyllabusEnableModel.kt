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
)

fun SyllabusEnableModel.compareToCourseSem(courseSem: String) = this.run {
    when (courseSem) {
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
        else -> {
            false
        }
    }
}
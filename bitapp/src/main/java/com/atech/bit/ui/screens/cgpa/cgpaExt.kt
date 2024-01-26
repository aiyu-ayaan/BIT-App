/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.cgpa

import com.atech.core.datasource.datastore.Cgpa


fun String.hasCgpaError(): Boolean = if (this.isNotEmpty()) {
    try {
        if (this.toDouble() > 10.0) true
        else false
    } catch (e: Exception) {
        true
    }
} else {
    false
}

fun String.hasCreditError(): Boolean = if (this.isNotEmpty()) {
    try {
        if (this.toDouble() > 100.0) true
        else false
    } catch (e: Exception) {
        true
    }
} else {
    false
}


data class CgpaEditModel(
    val semPlaceHolder: String = "",
    val semester: String = "",
    val earnCredit: String = ""
)

fun Cgpa.toPair() = listOf(
    sem1 to (earnCrSem1 ?: 0.0),
    sem2 to (earnCrSem2 ?: 0.0),
    sem3 to (earnCrSem3 ?: 0.0),
    sem4 to (earnCrSem4 ?: 0.0),
    sem5 to (earnCrSem5 ?: 0.0),
    sem6 to (earnCrSem6 ?: 0.0)
)

fun String.convertToCgpa(): Double = if (this.isNotEmpty()) {
    try {
        this.toDouble()
    } catch (e: Exception) {
        0.0
    }
} else {
    0.0
}


fun List<Pair<Double, Double>>.calculateCgpa(): Double {
    val totalCredits = this.sumOf { it.second }
    val totalPoints = this.sumOf { it.first * it.second }
    return String.format("%.2f", totalPoints / totalCredits).toDouble()
}

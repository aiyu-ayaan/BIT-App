package com.atech.bit.ui.screens.cgpa

import com.atech.core.datasource.datastore.Cgpa


data class CgpaEditModel(
    val semPlaceHolder: String = "",
    val semester: String = "",
    val gpa: String = ""
)

fun Cgpa.toPair() = listOf(
    sem1 to earnCrSem1,
    sem2 to earnCrSem2,
    sem3 to earnCrSem3,
    sem4 to earnCrSem4,
    sem5 to earnCrSem5,
    sem6 to earnCrSem6
)


fun Double.removeZeroAfterDecimal() =
    this.run {
        if (this.toString().contains(".0")) {
            this.toString().replace(".0", "")
        } else {
            this.toString()
        }
    }
package com.aatec.bit.utils

import android.util.Log
import androidx.annotation.Keep


@Keep
data class TotalCredit(val credit: Double)

val listOfBcaCredits = listOf(
    TotalCredit(18.0),
    TotalCredit(21.0),
    TotalCredit(19.0),
    TotalCredit(20.0),
    TotalCredit(22.0),
    TotalCredit(20.0),
)

val listOfBbaCredits = listOf(
    TotalCredit(18.0),
    TotalCredit(21.0),
    TotalCredit(21.0),
    TotalCredit(21.0),
    TotalCredit(22.0),
    TotalCredit(20.0),
)


data class BcaCourse(val credit: Double, val gradePoint: Double)

fun calculateCGPA(course: List<BcaCourse>): Double {
    var totalCredit = 1.0
    var totalGradePoint = 1.0
    course.forEach {
        totalCredit += it.credit
        Log.d("calculateCGPA:", "${it.credit}")
        totalGradePoint += it.credit * it.gradePoint
    }
    return totalGradePoint / totalCredit
}
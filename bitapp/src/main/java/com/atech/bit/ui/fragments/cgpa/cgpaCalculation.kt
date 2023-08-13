package com.atech.bit.ui.fragments.cgpa

fun List<Pair<Double, Double>>.calculateCgpa(): Double {
    val totalCredits = this.sumOf { it.second }
    val totalPoints = this.sumOf { it.first * it.second }
    return String.format("%.2f", totalPoints / totalCredits).toDouble()
}

fun List<Pair<Double, Double>>.toCgpaModel(): CgpaCalculatorFragment.CgpaPair {
    val defaultPair = 0.0 to 0.0
    val sem1 = getOrNull(0) ?: defaultPair
    val sem2 = getOrNull(1) ?: defaultPair
    val sem3 = getOrNull(2) ?: defaultPair
    val sem4 = getOrNull(3) ?: defaultPair
    val sem5 = getOrNull(4) ?: defaultPair
    val sem6 = getOrNull(5) ?: defaultPair
    return CgpaCalculatorFragment.CgpaPair(sem1, sem2, sem3, sem4, sem5, sem6)
}
/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/1/21, 1:09 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/1/21, 11:14 AM
 */



package com.atech.core.utils

fun findPercentage(present: Float, total: Float, action: (Float, Float) -> Float) =
    action(present, total)

fun setResources(percentage: Int, action: (Int) -> Unit) =
    action(percentage)

fun calculatedDays(present: Int, total: Int, action: (Float, Float) -> Float) =
    action(present.toFloat(), total.toFloat())


/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/1/21, 1:09 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/1/21, 11:14 AM
 */



package com.atech.syllabus

import android.content.Context
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

fun setFragment(@IdRes fragmentId: Int, context: Context, syllabusModel: String) {
    val fragment: Fragment = when {
        syllabusModel.lowercase().contains("bba") -> openCodeBBA1(syllabusModel)
        syllabusModel.lowercase().contains("bca") -> openCodeBCA1(syllabusModel)
        else -> {
            NoSyllabusFragment()
        }
    }
    val fm = (context as AppCompatActivity).supportFragmentManager
    fm.beginTransaction()
        .replace(fragmentId, fragment)
        .commit()
}

fun setFragment(@IdRes fragmentId: Int, fm: FragmentManager, syllabusModel: String) {
    val fragment: Fragment = when {
        syllabusModel.lowercase().contains("bba") -> openCodeBBA1(syllabusModel)
        syllabusModel.lowercase().contains("bca") -> openCodeBCA1(syllabusModel)
        else -> {
            NoSyllabusFragment()
        }
    }

    fm.beginTransaction()
        .replace(fragmentId, fragment)
        .commit()
}

fun openCodeBBA1(syllabusModel: String): Fragment =
    when {
        //        BBA
//        sem 1
        syllabusModel.lowercase() == "bba11" -> FragBba11()
        syllabusModel.lowercase() == "bba12" -> FragBba12()
        syllabusModel.lowercase() == "bba13" -> FragBba13()
        syllabusModel.lowercase() == "bba14" -> FragBba14()
        syllabusModel.lowercase() == "bba15" -> FragBba15()
        syllabusModel.lowercase() == "bba16" -> FragBba16()

//        sem 2
        syllabusModel.lowercase() == "bba21" -> FragBba21()
        syllabusModel.lowercase() == "bba22" -> FragBba22()
        syllabusModel.lowercase() == "bba23" -> FragBba23()
        syllabusModel.lowercase() == "bba24" -> FragBba24()
        syllabusModel.lowercase() == "bba25" -> FragBba25()
        syllabusModel.lowercase() == "bba26" -> FragBba26()
        syllabusModel.lowercase() == "bba27" -> FragBba27()

//sem 3
        syllabusModel.lowercase() == "bba31" -> FragBba31()
        syllabusModel.lowercase() == "bba32" -> FragBba32()
        syllabusModel.lowercase() == "bba33" -> FragBba33()
        syllabusModel.lowercase() == "bba34" -> FragBba34()
        syllabusModel.lowercase() == "bba35" -> FragBba35()
        syllabusModel.lowercase() == "bba36" -> FragBba36()
        syllabusModel.lowercase() == "bba37" -> FragBba37()
        syllabusModel.lowercase() == "bba38" -> FragBba38()


        //sem 4
        syllabusModel.lowercase() == "bba41" -> FragBba41()
        syllabusModel.lowercase() == "bba42" -> FragBba42()
        syllabusModel.lowercase() == "bba43" -> FragBba43()
        syllabusModel.lowercase() == "bba44" -> FragBba44()
        syllabusModel.lowercase() == "bba45" -> FragBba45()
        syllabusModel.lowercase() == "bba46" -> FragBba46()
        syllabusModel.lowercase() == "bba47" -> FragBba47()

        //sem 5
        syllabusModel.lowercase() == "bba51" -> FragBba51()
        syllabusModel.lowercase() == "bba52" -> FragBba52()
        syllabusModel.lowercase() == "bba53" -> FragBba53()
        syllabusModel.lowercase() == "bba54" -> FragBba54()
        syllabusModel.lowercase() == "bba55" -> FragBba55()
        syllabusModel.lowercase() == "bba56" -> FragBba56()
        syllabusModel.lowercase() == "bba57" -> FragBba57()
        syllabusModel.lowercase() == "bba58" -> FragBba58()
        syllabusModel.lowercase() == "bba59" -> FragBba59()


        //sem 6
        syllabusModel.lowercase() == "bba61" -> FragBba61()
        syllabusModel.lowercase() == "bba62" -> FragBba62()
        syllabusModel.lowercase() == "bba65" -> FragBba65()
        syllabusModel.lowercase() == "bba66" -> FragBba66()
        syllabusModel.lowercase() == "bba67" -> FragBba68()
        syllabusModel.lowercase() == "bba68" -> FragBba69()
        syllabusModel.lowercase() == "bba610" -> FragBba610()
        syllabusModel.lowercase() == "bba612" -> FragBba67()
        syllabusModel.lowercase() == "bba613" -> FragBba613()
        syllabusModel.lowercase() == "bba614" -> FragBba614()
        syllabusModel.lowercase() == "bba615" -> FragBba615()
        syllabusModel.lowercase() == "bba616" -> FragBba616()
        syllabusModel.lowercase() == "bba617" -> FragBba617()
        syllabusModel.lowercase() == "bba618" -> FragBba618()
        else -> NoSyllabusFragment()
    }

fun openCodeBCA1(syllabusModel: String): Fragment =
    when {
        //        BCA
//        sem 1
        syllabusModel.lowercase() == "bca11" -> FragBca11()
        syllabusModel.lowercase() == "bca12" -> FragBca12()
        syllabusModel.lowercase() == "bca13" -> FragBca13()
        syllabusModel.lowercase() == "bca14" -> FragBca14()
        syllabusModel.lowercase() == "bca15" -> FragBca15()
        syllabusModel.lowercase() == "bca16" -> FragBca16()
        syllabusModel.lowercase() == "bca17" -> FragBca17()
//        sem 2
        syllabusModel.lowercase() == "bca21" -> FragBca21()
        syllabusModel.lowercase() == "bca22" -> FragBca22()
        syllabusModel.lowercase() == "bca23" -> FragBca23()
        syllabusModel.lowercase() == "bca24" -> FragBca24()
        syllabusModel.lowercase() == "bca25" -> FragBca25()
        syllabusModel.lowercase() == "bca26" -> FragBca26()
        syllabusModel.lowercase() == "bca27" -> FragBca27()
        syllabusModel.lowercase() == "bca28" -> FragBca28()

        //sem 3
        syllabusModel.lowercase() == "bca31" -> FragBca31()
        syllabusModel.lowercase() == "bca32" -> FragBca32()
        syllabusModel.lowercase() == "bca33" -> FragBca33()
        syllabusModel.lowercase() == "bca34" -> FragBca34()
        syllabusModel.lowercase() == "bca35" -> FragBca35()
        syllabusModel.lowercase() == "bca36" -> FragBca36()
        syllabusModel.lowercase() == "bca37" -> FragBca37()
        syllabusModel.lowercase() == "bca38" -> FragBca38()


        //sem 4
        syllabusModel.lowercase() == "bca41" -> FragBca41()
        syllabusModel.lowercase() == "bca42" -> FragBca42()
        syllabusModel.lowercase() == "bca43" -> FragBca43()
        syllabusModel.lowercase() == "bca44" -> FragBca44()
        syllabusModel.lowercase() == "bca45" -> FragBca45()
        syllabusModel.lowercase() == "bca46" -> FragBca46()
        syllabusModel.lowercase() == "bca47" -> FragBca47()
        syllabusModel.lowercase() == "bca48" -> FragBca48()
        syllabusModel.lowercase() == "bca49" -> FragBca49()
        syllabusModel.lowercase() == "bca410" -> FragBca410()
        syllabusModel.lowercase() == "bca411" -> FragBca411()

        //sem 5
        syllabusModel.lowercase() == "bca51" -> FragBca51()
        syllabusModel.lowercase() == "bca52" -> FragBca52()
        syllabusModel.lowercase() == "bca53" -> FragBca53()
        syllabusModel.lowercase() == "bca54" -> FragBca54()
        syllabusModel.lowercase() == "bca55" -> FragBca55()
        syllabusModel.lowercase() == "bca56" -> FragBca56()
        syllabusModel.lowercase() == "bca57" -> FragBca57()
        syllabusModel.lowercase() == "bca58" -> FragBca58()
        syllabusModel.lowercase() == "bca59" -> FragBca59()
        syllabusModel.lowercase() == "bca510" -> FragBca510()
        syllabusModel.lowercase() == "bca511" -> FragBca511()
        syllabusModel.lowercase() == "bca512" -> FragBca512()


        //sem 6
        syllabusModel.lowercase() == "bca61" -> FragBca61()
        syllabusModel.lowercase() == "bca62" -> FragBca62()
        syllabusModel.lowercase() == "bca63" -> FragBca63()
        syllabusModel.lowercase() == "bca64" -> FragBca67()
        syllabusModel.lowercase() == "bca65" -> FragBca68()
        syllabusModel.lowercase() == "bca66" -> FragBca66()
        syllabusModel.lowercase() == "bca67" -> FragBca65()
        syllabusModel.lowercase() == "bca68" -> FragBca64()
        else -> NoSyllabusFragment()
    }
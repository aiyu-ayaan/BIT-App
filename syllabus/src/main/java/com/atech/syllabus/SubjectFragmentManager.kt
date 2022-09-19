/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/1/21, 1:09 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/1/21, 11:14 AM
 */



package com.atech.syllabus

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.atech.core.data.room.syllabus.SyllabusModel

fun setNoSyllabusFragment(@IdRes fragmentId: Int, fm: FragmentManager) {
    val fragment = NoSyllabusFragment()
    fm.beginTransaction()
        .replace(fragmentId, fragment)
        .commit()
}

fun setFragment(@IdRes fragmentId: Int, fm: FragmentManager, syllabusModel: SyllabusModel) {
    val fragment: Fragment = when {
        syllabusModel.openCode.lowercase().contains("bba") -> openCodeBBA(syllabusModel)
        syllabusModel.openCode.lowercase().contains("bca") -> openCodeBCA(syllabusModel)
        else -> FragmentNetworkSyllabus(syllabusModel)
    }

    fm.beginTransaction()
        .replace(fragmentId, fragment)
        .commit()
}

fun openCodeBBA(syllabusModel: SyllabusModel): Fragment =
    when {
        //        BBA
//        sem 1
        syllabusModel.openCode.lowercase() == "bba11" -> FragBba11()
        syllabusModel.openCode.lowercase() == "bba12" -> FragBba12()
        syllabusModel.openCode.lowercase() == "bba13" -> FragBba13()
        syllabusModel.openCode.lowercase() == "bba14" -> FragBba14()
        syllabusModel.openCode.lowercase() == "bba15" -> FragBba15()
        syllabusModel.openCode.lowercase() == "bba16" -> FragBba16()

//        sem 2
        syllabusModel.openCode.lowercase() == "bba21" -> FragBba21()
        syllabusModel.openCode.lowercase() == "bba22" -> FragBba22()
        syllabusModel.openCode.lowercase() == "bba23" -> FragBba23()
        syllabusModel.openCode.lowercase() == "bba24" -> FragBba24()
        syllabusModel.openCode.lowercase() == "bba25" -> FragBba25()
        syllabusModel.openCode.lowercase() == "bba26" -> FragBba26()
        syllabusModel.openCode.lowercase() == "bba27" -> FragBba27()

//sem 3
        syllabusModel.openCode.lowercase() == "bba31" -> FragBba31()
        syllabusModel.openCode.lowercase() == "bba32" -> FragBba32()
        syllabusModel.openCode.lowercase() == "bba33" -> FragBba33()
        syllabusModel.openCode.lowercase() == "bba34" -> FragBba34()
        syllabusModel.openCode.lowercase() == "bba35" -> FragBba35()
        syllabusModel.openCode.lowercase() == "bba36" -> FragBba36()
        syllabusModel.openCode.lowercase() == "bba37" -> FragBba37()
        syllabusModel.openCode.lowercase() == "bba38" -> FragBba38()


        //sem 4
        syllabusModel.openCode.lowercase() == "bba41" -> FragBba41()
        syllabusModel.openCode.lowercase() == "bba42" -> FragBba42()
        syllabusModel.openCode.lowercase() == "bba43" -> FragBba43()
        syllabusModel.openCode.lowercase() == "bba44" -> FragBba44()
        syllabusModel.openCode.lowercase() == "bba45" -> FragBba45()
        syllabusModel.openCode.lowercase() == "bba46" -> FragBba46()
        syllabusModel.openCode.lowercase() == "bba47" -> FragBba47()

        //sem 5
        syllabusModel.openCode.lowercase() == "bba51" -> FragBba51()
        syllabusModel.openCode.lowercase() == "bba52" -> FragBba52()
        syllabusModel.openCode.lowercase() == "bba53" -> FragBba53()
        syllabusModel.openCode.lowercase() == "bba54" -> FragBba54()
        syllabusModel.openCode.lowercase() == "bba55" -> FragBba55()
        syllabusModel.openCode.lowercase() == "bba56" -> FragBba56()
        syllabusModel.openCode.lowercase() == "bba57" -> FragBba57()
        syllabusModel.openCode.lowercase() == "bba58" -> FragBba58()
        syllabusModel.openCode.lowercase() == "bba59" -> FragBba59()


        //sem 6
        syllabusModel.openCode.lowercase() == "bba61" -> FragBba61()
        syllabusModel.openCode.lowercase() == "bba62" -> FragBba62()
        syllabusModel.openCode.lowercase() == "bba65" -> FragBba65()
        syllabusModel.openCode.lowercase() == "bba66" -> FragBba66()
        syllabusModel.openCode.lowercase() == "bba67" -> FragBba68()
        syllabusModel.openCode.lowercase() == "bba68" -> FragBba69()
        syllabusModel.openCode.lowercase() == "bba610" -> FragBba610()
        syllabusModel.openCode.lowercase() == "bba612" -> FragBba67()
        syllabusModel.openCode.lowercase() == "bba613" -> FragBba613()
        syllabusModel.openCode.lowercase() == "bba614" -> FragBba614()
        syllabusModel.openCode.lowercase() == "bba615" -> FragBba615()
        syllabusModel.openCode.lowercase() == "bba616" -> FragBba616()
        syllabusModel.openCode.lowercase() == "bba617" -> FragBba617()
        syllabusModel.openCode.lowercase() == "bba618" -> FragBba618()
        else -> FragmentNetworkSyllabus(syllabusModel)
    }

fun openCodeBCA(syllabusModel: SyllabusModel): Fragment =
    when {
        //        BCA
//        sem 1
        syllabusModel.openCode.lowercase() == "bca11" -> FragBca11()
        syllabusModel.openCode.lowercase() == "bca12" -> FragBca12()
        syllabusModel.openCode.lowercase() == "bca13" -> FragBca13()
        syllabusModel.openCode.lowercase() == "bca14" -> FragBca14()
        syllabusModel.openCode.lowercase() == "bca15" -> FragBca15()
        syllabusModel.openCode.lowercase() == "bca16" -> FragBca16()
        syllabusModel.openCode.lowercase() == "bca17" -> FragBca17()
//        sem 2
        syllabusModel.openCode.lowercase() == "bca21" -> FragBca21()
        syllabusModel.openCode.lowercase() == "bca22" -> FragBca22()
        syllabusModel.openCode.lowercase() == "bca23" -> FragBca23()
        syllabusModel.openCode.lowercase() == "bca24" -> FragBca24()
        syllabusModel.openCode.lowercase() == "bca25" -> FragBca25()
        syllabusModel.openCode.lowercase() == "bca26" -> FragBca26()
        syllabusModel.openCode.lowercase() == "bca27" -> FragBca27()
        syllabusModel.openCode.lowercase() == "bca28" -> FragBca28()

        //sem 3
        syllabusModel.openCode.lowercase() == "bca31" -> FragBca31()
        syllabusModel.openCode.lowercase() == "bca32" -> FragBca32()
        syllabusModel.openCode.lowercase() == "bca33" -> FragBca33()
        syllabusModel.openCode.lowercase() == "bca34" -> FragBca34()
        syllabusModel.openCode.lowercase() == "bca35" -> FragBca35()
        syllabusModel.openCode.lowercase() == "bca36" -> FragBca36()
        syllabusModel.openCode.lowercase() == "bca37" -> FragBca37()
        syllabusModel.openCode.lowercase() == "bca38" -> FragBca38()


        //sem 4
        syllabusModel.openCode.lowercase() == "bca41" -> FragBca41()
        syllabusModel.openCode.lowercase() == "bca42" -> FragBca42()
        syllabusModel.openCode.lowercase() == "bca43" -> FragBca43()
        syllabusModel.openCode.lowercase() == "bca44" -> FragBca44()
        syllabusModel.openCode.lowercase() == "bca45" -> FragBca45()
        syllabusModel.openCode.lowercase() == "bca46" -> FragBca46()
        syllabusModel.openCode.lowercase() == "bca47" -> FragBca47()
        syllabusModel.openCode.lowercase() == "bca48" -> FragBca48()
        syllabusModel.openCode.lowercase() == "bca49" -> FragBca49()
        syllabusModel.openCode.lowercase() == "bca410" -> FragBca410()
        syllabusModel.openCode.lowercase() == "bca411" -> FragBca411()

        //sem 5
        syllabusModel.openCode.lowercase() == "bca51" -> FragBca51()
        syllabusModel.openCode.lowercase() == "bca52" -> FragBca52()
        syllabusModel.openCode.lowercase() == "bca53" -> FragBca53()
        syllabusModel.openCode.lowercase() == "bca54" -> FragBca54()
        syllabusModel.openCode.lowercase() == "bca55" -> FragBca55()
        syllabusModel.openCode.lowercase() == "bca56" -> FragBca56()
        syllabusModel.openCode.lowercase() == "bca57" -> FragBca57()
        syllabusModel.openCode.lowercase() == "bca58" -> FragBca58()
        syllabusModel.openCode.lowercase() == "bca59" -> FragBca59()
        syllabusModel.openCode.lowercase() == "bca510" -> FragBca510()
        syllabusModel.openCode.lowercase() == "bca511" -> FragBca511()
        syllabusModel.openCode.lowercase() == "bca512" -> FragBca512()


        //sem 6
        syllabusModel.openCode.lowercase() == "bca61" -> FragBca61()
        syllabusModel.openCode.lowercase() == "bca62" -> FragBca62()
        syllabusModel.openCode.lowercase() == "bca63" -> FragBca63()
        syllabusModel.openCode.lowercase() == "bca64" -> FragBca67()
        syllabusModel.openCode.lowercase() == "bca65" -> FragBca68()
        syllabusModel.openCode.lowercase() == "bca66" -> FragBca66()
        syllabusModel.openCode.lowercase() == "bca67" -> FragBca65()
        syllabusModel.openCode.lowercase() == "bca68" -> FragBca64()
        else -> FragmentNetworkSyllabus(syllabusModel)
    }
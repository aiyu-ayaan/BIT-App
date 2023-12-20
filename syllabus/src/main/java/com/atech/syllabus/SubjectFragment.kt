/*

 * BIT Lalpur App
 *
 * Created by Ayaan on 9/1/21, 1:09 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/1/21, 11:14 AM
 */
package com.atech.syllabus

import android.util.Log


fun getFragment(syllabusModel: String) =
    when {
        syllabusModel.lowercase().contains("bba") -> openCodeBBA(syllabusModel)
        syllabusModel.lowercase().contains("bca") -> openCodeBCA(syllabusModel)
        else -> {
            R.layout.fragment_no_sylabus_found
        }
    }


fun openCodeBBA(syllabusModel: String): Int =
    when {
        //        BBA
//        sem 1
        syllabusModel.lowercase() == "bba11" -> R.layout.fragment_bba_1_1
        syllabusModel.lowercase() == "bba12" -> R.layout.fragment_bba_1_2
        syllabusModel.lowercase() == "bba13" -> R.layout.fragment_bba_1_3
        syllabusModel.lowercase() == "bba14" -> R.layout.fragment_bba_1_4
        syllabusModel.lowercase() == "bba15" -> R.layout.fragment_bba_1_5
        syllabusModel.lowercase() == "bba16" -> R.layout.fragment_bba_1_6

//        sem 2
        syllabusModel.lowercase() == "bba21" -> R.layout.fragment_bba_2_1
        syllabusModel.lowercase() == "bba22" -> R.layout.fragment_bba_2_2
        syllabusModel.lowercase() == "bba23" -> R.layout.fragment_bba_2_3
        syllabusModel.lowercase() == "bba24" -> R.layout.fragment_bba_2_4
        syllabusModel.lowercase() == "bba25" -> R.layout.fragment_bba_2_5
        syllabusModel.lowercase() == "bba26" -> R.layout.fragment_bba_2_6
        syllabusModel.lowercase() == "bba27" -> R.layout.fragment_bba_2_7

//sem 3
        syllabusModel.lowercase() == "bba31" -> R.layout.fragment_bba_3_1
        syllabusModel.lowercase() == "bba32" -> R.layout.fragment_bba_3_2
        syllabusModel.lowercase() == "bba33" -> R.layout.fragment_bba_3_3
        syllabusModel.lowercase() == "bba34" -> R.layout.fragment_bba_3_4
        syllabusModel.lowercase() == "bba35" -> R.layout.fragment_bba_3_5
        syllabusModel.lowercase() == "bba36" -> R.layout.fragment_bba_3_6
        syllabusModel.lowercase() == "bba37" -> R.layout.fragment_bba_3_7
        syllabusModel.lowercase() == "bba38" -> R.layout.fragment_bba_3_8


        //sem 4
        syllabusModel.lowercase() == "bba41" -> R.layout.fragment_bba_4_1
        syllabusModel.lowercase() == "bba42" -> R.layout.fragment_bba_4_2
        syllabusModel.lowercase() == "bba43" -> R.layout.fragment_bba_4_3
        syllabusModel.lowercase() == "bba44" -> R.layout.fragment_bba_4_4
        syllabusModel.lowercase() == "bba45" -> R.layout.fragment_bba_4_5
        syllabusModel.lowercase() == "bba46" -> R.layout.fragment_bba_4_6
        syllabusModel.lowercase() == "bba47" -> R.layout.fragment_bba_4_7

        //sem 5
        syllabusModel.lowercase() == "bba51" -> R.layout.fragment_bba_5_1
        syllabusModel.lowercase() == "bba52" -> R.layout.fragment_bba_5_2
        syllabusModel.lowercase() == "bba53" -> R.layout.fragment_bba_5_3
        syllabusModel.lowercase() == "bba54" -> R.layout.fragment_bba_5_4
        syllabusModel.lowercase() == "bba55" -> R.layout.fragment_bba_5_5
        syllabusModel.lowercase() == "bba56" -> R.layout.fragment_bba_5_6
        syllabusModel.lowercase() == "bba57" -> R.layout.fragment_bba_5_7
        syllabusModel.lowercase() == "bba58" -> R.layout.fragment_bba_5_8
        syllabusModel.lowercase() == "bba59" -> R.layout.fragment_bba_5_9


        //sem 6
        syllabusModel.lowercase() == "bba61" -> R.layout.fragment_bba_6_1
        syllabusModel.lowercase() == "bba62" -> R.layout.fragment_bba_6_2
        syllabusModel.lowercase() == "bba65" -> R.layout.fragment_bba_6_5
        syllabusModel.lowercase() == "bba66" -> R.layout.fragment_bba_6_6
        syllabusModel.lowercase() == "bba67" -> R.layout.fragment_bba_6_8
        syllabusModel.lowercase() == "bba68" -> R.layout.fragment_bba_6_9
        syllabusModel.lowercase() == "bba610" -> R.layout.fragment_bba_6_10
        syllabusModel.lowercase() == "bba612" -> R.layout.fragment_bba_6_7
        syllabusModel.lowercase() == "bba613" -> R.layout.fragment_bba_6_13
        syllabusModel.lowercase() == "bba614" -> R.layout.fragment_bba_6_14
        syllabusModel.lowercase() == "bba615" -> R.layout.fragment_bba_6_15
        syllabusModel.lowercase() == "bba616" -> R.layout.fragment_bba_6_16
        syllabusModel.lowercase() == "bba617" -> R.layout.fragment_bba_6_17
        syllabusModel.lowercase() == "bba618" -> R.layout.fragment_bba_6_18
        else -> R.layout.fragment_no_sylabus_found
    }

fun openCodeBCA(syllabusModel: String): Int =
    when {
        //        BCA
//        sem 1
        syllabusModel.lowercase() == "bca11" -> R.layout.fragment_bca_1_1
        syllabusModel.lowercase() == "bca12" -> R.layout.fragment_bca_1_2
        syllabusModel.lowercase() == "bca13" -> R.layout.fragment_bca_1_3
        syllabusModel.lowercase() == "bca14" -> R.layout.fragment_bca_1_4
        syllabusModel.lowercase() == "bca15" -> R.layout.fragment_bca_1_5
        syllabusModel.lowercase() == "bca16" -> R.layout.fragment_bca_1_6
        syllabusModel.lowercase() == "bca17" -> R.layout.fragment_bca_1_7
//        sem 2
        syllabusModel.lowercase() == "bca21" -> R.layout.fragment_bca_2_1
        syllabusModel.lowercase() == "bca22" -> R.layout.fragment_bca_2_2
        syllabusModel.lowercase() == "bca23" -> R.layout.fragment_bca_2_3
        syllabusModel.lowercase() == "bca24" -> R.layout.fragment_bca_2_4
        syllabusModel.lowercase() == "bca25" -> R.layout.fragment_bca_2_5
        syllabusModel.lowercase() == "bca26" -> R.layout.fragment_bca_2_6
        syllabusModel.lowercase() == "bca27" -> R.layout.fragment_bca_2_7
        syllabusModel.lowercase() == "bca28" -> R.layout.fragment_bca_2_8

        //sem 3
        syllabusModel.lowercase() == "bca31" -> R.layout.fragment_bca_3_1
        syllabusModel.lowercase() == "bca32" -> R.layout.fragment_bca_3_2
        syllabusModel.lowercase() == "bca33" -> R.layout.fragment_bca_3_3
        syllabusModel.lowercase() == "bca34" -> R.layout.fragment_bca_3_4
        syllabusModel.lowercase() == "bca35" -> R.layout.fragment_bca_3_5
        syllabusModel.lowercase() == "bca36" -> R.layout.fragment_bca_3_6
        syllabusModel.lowercase() == "bca37" -> R.layout.fragment_bca_3_7
        syllabusModel.lowercase() == "bca38" -> R.layout.fragment_bca_3_8


        //sem 4
        syllabusModel.lowercase() == "bca41" -> R.layout.fragment_bca_4_1
        syllabusModel.lowercase() == "bca42" -> R.layout.fragment_bca_4_2
        syllabusModel.lowercase() == "bca43" -> R.layout.fragment_bca_4_3
        syllabusModel.lowercase() == "bca44" -> R.layout.fragment_bca_4_4
        syllabusModel.lowercase() == "bca45" -> R.layout.fragment_bca_4_5
        syllabusModel.lowercase() == "bca46" -> R.layout.fragment_bca_4_6
        syllabusModel.lowercase() == "bca47" -> R.layout.fragment_bca_4_7
        syllabusModel.lowercase() == "bca48" -> R.layout.fragment_bca_4_8
        syllabusModel.lowercase() == "bca49" -> R.layout.fragment_bca_4_9
        syllabusModel.lowercase() == "bca410" -> R.layout.fragment_bca_4_10
        syllabusModel.lowercase() == "bca411" -> R.layout.fragment_bca_4_11

        //sem 5
        syllabusModel.lowercase() == "bca51" -> R.layout.fragment_bca_5_1
        syllabusModel.lowercase() == "bca52" -> R.layout.fragment_bca_5_2
        syllabusModel.lowercase() == "bca53" -> R.layout.fragment_bca_5_3
        syllabusModel.lowercase() == "bca54" -> R.layout.fragment_bca_5_4
        syllabusModel.lowercase() == "bca55" -> R.layout.fragment_bca_5_5
        syllabusModel.lowercase() == "bca56" -> R.layout.fragment_bca_5_6
        syllabusModel.lowercase() == "bca57" -> R.layout.fragment_bca_5_7
        syllabusModel.lowercase() == "bca58" -> R.layout.fragment_bca_5_8
        syllabusModel.lowercase() == "bca59" -> R.layout.fragment_bca_5_9
        syllabusModel.lowercase() == "bca510" -> R.layout.fragment_bca_5_10
        syllabusModel.lowercase() == "bca511" -> R.layout.fragment_bca_5_11
        syllabusModel.lowercase() == "bca512" -> R.layout.fragment_bca_5_12


        //sem 6
        syllabusModel.lowercase() == "bca61" -> R.layout.fragment_bca_6_1
        syllabusModel.lowercase() == "bca62" -> R.layout.fragment_bca_6_2
        syllabusModel.lowercase() == "bca63" -> R.layout.fragment_bca_6_3
        syllabusModel.lowercase() == "bca64" -> R.layout.fragment_bca_6_4
        syllabusModel.lowercase() == "bca65" -> R.layout.fragment_bca_6_5
        syllabusModel.lowercase() == "bca66" -> R.layout.fragment_bca_6_6
        syllabusModel.lowercase() == "bca67" -> R.layout.fragment_bca_6_7
        syllabusModel.lowercase() == "bca68" -> R.layout.fragment_bca_6_8
        else -> R.layout.fragment_no_sylabus_found
    }
package com.atech.course.utils

import com.google.android.material.tabs.TabLayout

inline fun TabLayout.tabSelectedListener(crossinline listener: (TabLayout.Tab?) -> Unit) {
    addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) = listener(tab)
        override fun onTabUnselected(tab: TabLayout.Tab?) = Unit
        override fun onTabReselected(tab: TabLayout.Tab?) = Unit
    })
}
package com.atech.theme

import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView

interface ParentActivity {

    fun setDrawerState(isOpen: Boolean)

    fun toggleDrawer()

    fun getDrawerLayout(): DrawerLayout

    fun setBottomNavigationVisibility(isVisible: Boolean)

    fun getVersionName(): String


    fun getNavigationFragmentId(): Int

    fun getBottomNavigationFragment(): BottomNavigationView
}
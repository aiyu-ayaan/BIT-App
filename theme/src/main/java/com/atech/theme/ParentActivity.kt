package com.atech.theme

import androidx.drawerlayout.widget.DrawerLayout

interface ParentActivity {

    fun setDrawerState(isOpen: Boolean)

    fun toggleDrawer()

    fun getDrawerLayout(): DrawerLayout

    fun setBottomNavigationVisibility(isVisible: Boolean)
}
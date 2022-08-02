package com.atech.bit.utils

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.annotation.MenuRes
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle


inline fun Fragment.addMenuHost(
    @MenuRes id: Int? = null,
    crossinline getMenu: (Menu) -> Unit = {},
    crossinline menuClick: ((MenuItem) -> Boolean) = { false }
): MenuProvider =
    this.run {
        val menuHost = requireActivity()
        val menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                id?.let {
                    menuInflater.inflate(it, menu)
                }
                getMenu.invoke(menu)

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                menuClick.invoke(menuItem)


        }
        menuHost.addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)
        menuProvider
    }
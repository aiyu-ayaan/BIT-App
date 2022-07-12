package com.aatec.bit.utils

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.annotation.MenuRes
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle


inline fun Fragment.addMenuHost(
    @MenuRes id: Int? =null,
    crossinline menuClick: ((MenuItem) -> Boolean) = { false }
) =
    this.apply {
        val menuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                id?.let {
                    menuInflater.inflate(it, menu)
                }

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                menuClick.invoke(menuItem)


        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
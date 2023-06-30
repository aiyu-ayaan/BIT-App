package com.atech.bit.ui.fragments.home

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.atech.bit.R
import com.atech.bit.databinding.FragmentHomeBinding
import com.atech.bit.ui.activities.main_activity.MainActivity
import com.atech.theme.customBackPress
import com.atech.theme.enterTransition
import com.google.android.material.navigation.NavigationView
import com.google.android.material.search.SearchView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private val binding: FragmentHomeBinding by viewBinding()

    private val mainActivity: MainActivity by lazy {
        requireActivity() as MainActivity
    }
    private val drawerLayout: DrawerLayout by lazy {
        mainActivity.findViewById(R.id.drawerLayout)
    }
    private val navView: NavigationView by lazy {
        mainActivity.findViewById(R.id.navigationView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            setDrawerOpen()
        }
        handleDrawer()
        handleExpand()
        handleBackPress()
    }

    private fun FragmentHomeBinding.setDrawerOpen() = this.searchBar.setNavigationOnClickListener {
        setDrawerState(true)
    }


    private fun handleDrawer() = navView.apply {
        navView.setupWithNavController(mainActivity.navController)
        setNavigationItemSelectedListener { menu ->
            setDrawerState(false)
            when (menu.itemId) {
                else -> NavigationUI.onNavDestinationSelected(menu, mainActivity.navController)
            }
            true
        }
    }

    private fun setDrawerState(isOpen: Boolean) = this.drawerLayout.apply {
        if (isOpen) openDrawer(GravityCompat.START)
        else closeDrawer(GravityCompat.START)
    }


    private fun handleExpand() {
        binding.searchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.SHOWING) {
                mainActivity.setBottomNavigationVisibility(false)
            }
            if (newState == SearchView.TransitionState.HIDING) {
                mainActivity.setBottomNavigationVisibility(true)
            }
        }
    }

    private fun handleBackPress() {
        customBackPress {
            when {
                drawerLayout.isDrawerOpen(GravityCompat.START) -> {
                    setDrawerState(false)
                }

                else -> {
                    if (binding.searchView.isShowing) {
                        binding.searchView.hide()
                    } else {
                        activity?.finish()
                    }
                }
            }
        }
    }

}
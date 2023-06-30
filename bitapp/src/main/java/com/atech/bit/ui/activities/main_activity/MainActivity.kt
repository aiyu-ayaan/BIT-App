package com.atech.bit.ui.activities.main_activity

import android.graphics.Color
import android.os.Bundle
import android.viewbinding.library.activity.viewBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.atech.bit.R
import com.atech.bit.databinding.ActivityMainBinding
import com.atech.bit.utils.onDestinationChange
import com.atech.theme.changeBottomNav
import com.atech.theme.changeStatusBarToolbarColorImageView
import com.atech.theme.currentNavigationFragment
import com.atech.theme.exitTransition
import com.atech.theme.isDark
import com.atech.theme.setStatusBarUiTheme
import com.google.android.material.color.MaterialColors
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by viewBinding()
    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
    }
    val navController by lazy {
        navHostFragment.navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.apply {
            bottomNavigationSetup()
        }
        handleDestinationChange()
    }

    private fun ActivityMainBinding.bottomNavigationSetup() {
        bottomNavigation.setupWithNavController(navController)
        bottomNavigation.setOnItemReselectedListener { }
    }

    private fun handleDestinationChange() {
        navController.onDestinationChange { destination ->
//            setExitTransition()
            when (destination.id) {
                in baseFragments() -> {
                    setBottomNavigationVisibility(true)
                    changeBottomNav(
                        com.atech.theme.R.attr.bottomBar
                    )
                }

                com.atech.attendance.R.id.attendanceFragment -> {
                    setBottomNavigationVisibility(false)
                    changeBottomNav(
                        com.atech.theme.R.attr.bottomBar
                    )
                }

                else -> {
                    setBottomNavigationVisibility(false)
                }
            }
            when (destination.id) {
                in navigationViewFragments() ->
                    changeStatusBarToolbarColorImageView(
                        MaterialColors.getColor(
                            this, com.atech.theme.R.attr.bottomBar, Color.WHITE
                        ).also {
                            setStatusBarUiTheme(this, !isDark())
                        })

                else ->
                    changeStatusBarToolbarColorImageView(
                        MaterialColors.getColor(
                            this, android.viewbinding.library.R.attr.colorSurface, Color.WHITE
                        ).also {
                            setStatusBarUiTheme(this, !isDark())
                        })

            }
        }
    }

    fun setBottomNavigationVisibility(isVisible: Boolean) {
        binding.bottomNavigation.isVisible = isVisible
    }

    private fun setExitTransition() =
        supportFragmentManager.currentNavigationFragment?.exitTransition()

    private fun baseFragments() = listOf(
        R.id.homeFragment, com.atech.course.R.id.courseFragment
    )

    private fun navigationViewFragments() = listOf(
        R.id.holidayFragment,
        R.id.societyFragment,
        R.id.societyDetailFragment
    )

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
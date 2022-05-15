package com.aatec.bit.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.viewbinding.library.activity.viewBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.aatec.bit.R
import com.aatec.bit.databinding.ActivityMainBinding
import com.aatec.bit.utils.changeBottomNav
import com.aatec.bit.utils.changeStatusBarToolbarColor
import com.aatec.bit.utils.onDestinationChange
import com.aatec.core.utils.KEY_FIRST_TIME_TOGGLE
import com.aatec.core.utils.currentNavigationFragment
import com.google.android.material.transition.MaterialSharedAxis
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by viewBinding()
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    @Inject
    lateinit var db: FirebaseFirestore

    @Inject
    lateinit var appUpdateManager: AppUpdateManager

    @Inject
    lateinit var pref: SharedPreferences

    @Inject
    lateinit var fcm: FirebaseMessaging

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.apply {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
            navController = navHostFragment.findNavController()
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.startUpFragment,
                    R.id.homeFragment,
                    R.id.courseFragment,
                    R.id.attendanceFragment,
                    R.id.noticeFragment
                ),
                drawer
            )
            setSupportActionBar(toolbar)
            bottomNavigation.setupWithNavController(navController)
            setupActionBarWithNavController(navController, appBarConfiguration)
            navigationView.setupWithNavController(navController)
        }
        onDestinationChange()
    }

    private fun onDestinationChange() {
        navController.onDestinationChange { destination ->

            when (destination.id) {
                R.id.homeFragment, R.id.noticeFragment, R.id.attendanceFragment,
                R.id.courseFragment,
                -> {
                    setExitTransition()
                }
            }

            when (destination.id) {
                R.id.chooseImageBottomSheet, R.id.chooseSemBottomSheet, R.id.semChooseFragment -> {
                    changeStatusBarToolbarColor(
                        R.id.toolbar,
                        R.attr.bottomBar
                    )
                }
                else -> changeStatusBarToolbarColor(
                    R.id.toolbar,
                    com.google.android.material.R.attr.colorSurface
                )
            }
            when (destination.id) {
                R.id.homeFragment, R.id.noticeFragment, R.id.courseFragment, R.id.attendanceFragment,
                R.id.chooseImageBottomSheet, R.id.chooseSemBottomSheet -> {
                    changeBottomNav(R.attr.bottomBar)
                }
                else -> {
                    changeBottomNav(android.viewbinding.library.R.attr.colorSurface)
                }
            }
            when (destination.id) {
                R.id.startUpFragment, R.id.noticeDetailFragment,
                R.id.chooseImageBottomSheet, R.id.subjectHandlerFragment,
                R.id.semChooseFragment -> {
                    hideBottomAppBar()
                    binding.toolbar.visibility = View.VISIBLE
                }
                else -> {
                    showBottomAppBar()
                }
            }
            val u = pref.getBoolean(KEY_FIRST_TIME_TOGGLE, false)
            if (destination.id == R.id.startUpFragment || (destination.id == R.id.chooseSemBottomSheet && !u) || destination.id == R.id.viewImageFragment) {
                binding.toolbar.visibility = View.GONE
                hideBottomAppBar()
            }
        }
    }

    private fun setExitTransition() {
        getCurrentFragment()?.apply {
            exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ true)
            reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false)
        }
    }

    private fun getCurrentFragment(): Fragment? =
        supportFragmentManager.currentNavigationFragment

    private fun showBottomAppBar() {
        binding.apply {
            binding.toolbar.visibility = View.VISIBLE
            bottomLayout.visibility = View.VISIBLE
            bottomLayout.animate().translationY(0f)
                .setDuration(resources.getInteger(R.integer.duration_small).toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    var isCanceled = false
                    override fun onAnimationEnd(animation: Animator?) {
                        if (isCanceled) return
                        bottomLayout.visibility = View.VISIBLE
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                        isCanceled = true
                    }
                })
        }
    }

    private fun hideBottomAppBar() {
        binding.apply {
            bottomLayout.isVisible = false
            bottomLayout.animate().translationY(bottomLayout.height.toFloat())
                .setDuration(resources.getInteger(R.integer.duration_small).toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    var isCanceled = false
                    override fun onAnimationEnd(animation: Animator?) {
                        if (isCanceled) return
                        bottomLayout.visibility = View.GONE
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                        isCanceled = true
                    }
                })
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
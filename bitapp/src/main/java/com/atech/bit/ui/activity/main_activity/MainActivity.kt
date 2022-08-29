package com.atech.bit.ui.activity.main_activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.viewbinding.library.activity.viewBinding
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.*
import com.atech.bit.BuildConfig
import com.atech.bit.NavGraphDirections
import com.atech.bit.R
import com.atech.bit.databinding.ActivityMainBinding
import com.atech.bit.ui.activity.main_activity.viewmodels.CommunicatorViewModel
import com.atech.bit.ui.activity.main_activity.viewmodels.PreferenceManagerViewModel
import com.atech.bit.utils.DrawerLocker
import com.atech.bit.utils.MenuClick
import com.atech.bit.utils.openShareLink
import com.atech.core.data.preferences.SearchPreference
import com.atech.core.data.room.attendance.AttendanceDao
import com.atech.core.utils.*
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), DrawerLocker, MenuClick {

    private val binding: ActivityMainBinding by viewBinding()
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val communicator: CommunicatorViewModel by viewModels()
    private val prefManager: PreferenceManagerViewModel by viewModels()
    private lateinit var searchPreference: SearchPreference
    private var reviewInfo: ReviewInfo? = null
    private lateinit var reviewManager: ReviewManager


    @Inject
    lateinit var attendanceDao: AttendanceDao

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
                    R.id.logInFragment,
                    R.id.startUpFragment,
                    R.id.homeFragment,
                    R.id.courseFragment,
                    R.id.attendanceFragment,
                    R.id.noticeFragment,
                    R.id.warningFragment
                ),
                drawer
            )
            setSupportActionBar(toolbar)
            bottomNavigation.setupWithNavController(navController)
            bottomNavigation.setOnItemSelectedListener {
                if (it.itemId == R.id.homeFragment)
                    navController.popBackStack(R.id.homeFragment, false)
                else
                    NavigationUI.onNavDestinationSelected(it, navController)
                true
            }
            setupActionBarWithNavController(navController, appBarConfiguration)
            navigationView.setupWithNavController(navController)
            navigationView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.nav_connect -> resources.getString(R.string.instaLink)
                        .openLinks(this@MainActivity, R.string.no_intent_available)
                    R.id.nav_share -> shareApp()
                    R.id.nav_bug -> this@MainActivity.openBugLink()
                    R.id.nav_erp -> this@MainActivity.openCustomChromeTab(resources.getString(R.string.erp_link))
                    R.id.nav_rate -> startReviewFlow()
                    else -> NavigationUI.onNavDestinationSelected(it, navController)
                }
                true
            }
        }
        openAboutUs()
        onDestinationChange()
        searchFragmentCommunication()
        checkForUpdate()
        val u = pref.getBoolean(KEY_DO_NOT_SHOW_AGAIN, false)
        if (!u)
            getWarning()
        shareReview()
    }

    private fun shareApp() {
        db.collection("Utils")
            .document("AppLogoShare")
            .addSnapshotListener { value, _ ->
                val title = value?.getString("appLogo")
                this@MainActivity.openShareLink(
                    title
                        ?: APP_LOGO_LINK
                )
            }
    }

    private fun shareReview() {
        reviewManager = ReviewManagerFactory.create(this)
        val managerInfoTask = reviewManager.requestReviewFlow()
        managerInfoTask.addOnCompleteListener { task ->
            if (task.isSuccessful)
                reviewInfo = task.result
            else
                Log.e(ERROR_LOG, "shareReview:  Can't open review manager")
        }
    }

    private fun startReviewFlow() {
        if (reviewInfo != null)
            reviewManager.launchReviewFlow(this, reviewInfo!!)
                .addOnCompleteListener {
                    Toast.makeText(this, "Review is completed", Toast.LENGTH_SHORT).show()
                }
        else
            openPlayStore(packageName)


    }

    private fun onDestinationChange() {
        navController.onDestinationChange { destination ->
            when (destination.id) {
                R.id.noticeFragment,
                R.id.attendanceFragment,
                R.id.homeFragment,
                R.id.courseFragment
                -> getCurrentFragment().apply {
                    setDrawerEnabled(true)
                }

                else -> getCurrentFragment().apply {
                    setDrawerEnabled(false)
                }

            }
            when (destination.id) {
                R.id.searchFragment, R.id.settingDialog -> binding.searchToolbar.isVisible =
                    true
                else -> binding.searchToolbar.isVisible = false
            }
            if (destination.id == R.id.searchFragment)
                binding.apply {
                    searchInput.isEnabled = true
                    searchInput.requestFocus()
                    when {
                        communicator.openFirst ->
                            showKeyboard()
                    }
                }
            else
                binding.searchInput.isEnabled = false
            when (destination.id) {
                R.id.homeFragment, R.id.noticeFragment, R.id.attendanceFragment,
                R.id.courseFragment, R.id.holidayFragment, R.id.societyFragment,
                R.id.eventFragment, R.id.aboutUsFragment,
                -> setExitTransition()
            }

            when (destination.id) {
                R.id.semChooseFragment, R.id.detailDevFragment,
                R.id.searchFragment, R.id.noticeDetailFragment,
                R.id.eventDetailFragment
                -> changeStatusBarToolbarColor(
                    R.id.toolbar,
                    R.attr.bottomBar
                )

                R.id.addEditSubjectBottomSheet, R.id.listAllBottomSheet,
                R.id.chooseSemBottomSheet, R.id.editSubjectBottomSheet,
                R.id.calenderViewBottomSheet, R.id.attendanceMenu,
                R.id.chooseImageBottomSheet
                -> changeStatusBarToolbarColor(
                    R.id.toolbar,
                    R.attr.bottomSheetBackground
                ).also {
                    setStatusBarUiTheme(this, !this.isDark())
                }
                R.id.logInFragment ->
                    changeStatusBarToolbarColorImageView(
                        MaterialColors.getColor(
                            this,
                            R.attr.appLogoBackground,
                            Color.WHITE
                        ).also {
                            setStatusBarUiTheme(this, false)
                        }
                    )

                else -> changeStatusBarToolbarColor(
                    R.id.toolbar,
                    com.google.android.material.R.attr.colorSurface
                ).also {
                    setStatusBarUiTheme(this, !this.isDark())
                }
            }
            when (destination.id) {
                R.id.homeFragment, R.id.noticeFragment, R.id.courseFragment, R.id.attendanceFragment,
                R.id.chooseImageBottomSheet, R.id.chooseSemBottomSheet,
                R.id.addEditSubjectBottomSheet, R.id.listAllBottomSheet,
                R.id.editSubjectBottomSheet, R.id.calenderViewBottomSheet,
                R.id.themeChangeDialog, R.id.changePercentageDialog,
                R.id.attendanceMenu
                -> changeBottomNav(R.attr.bottomBar)

                else ->
                    changeBottomNav(android.viewbinding.library.R.attr.colorSurface)
            }
            when (destination.id) {
                R.id.startUpFragment, R.id.noticeDetailFragment,
                R.id.chooseImageBottomSheet, R.id.subjectHandlerFragment,
                R.id.semChooseFragment, R.id.holidayFragment,
                R.id.aboutUsFragment, R.id.detailDevFragment,
                R.id.acknowledgementFragment, R.id.societyFragment,
                R.id.eventSocietyDescriptionFragment, R.id.eventFragment,
                R.id.eventDetailFragment, R.id.searchFragment,
                R.id.settingDialog, R.id.cgpaCalculatorFragment,
                R.id.viewVideoFragment, R.id.loadingDataFragment
                -> {
                    hideBottomAppBar()
                    binding.toolbar.visibility = View.VISIBLE
                }
                else -> {
                    showBottomAppBar()
                }
            }
            val u = pref.getBoolean(KEY_USER_DONE_SET_UP, false)
            if (destination.id == R.id.startUpFragment || (destination.id == R.id.chooseSemBottomSheet && !u)
                || destination.id == R.id.viewImageFragment || destination.id == R.id.warningFragment ||
                destination.id == R.id.viewVideoFragment || destination.id == R.id.logInFragment || destination.id == R.id.loadingDataFragment
            ) {
                binding.toolbar.visibility = View.GONE
                hideBottomAppBar()
            }
        }
    }

    private fun searchFragmentCommunication() {
        binding.searchInput.apply {

            /**
             * Open keyboard only on onCreated for first time
             */
            communicator.query.value = text.toString()
            lifecycleScope.launchWhenStarted {
                communicator.query.collect {
                    if (it.isBlank())
                        setText(getString(R.string.blank))
                }
            }
            addTextChangedListener {
                communicator.query.value = if (it?.isBlank() == true) getString(R.string.blank)
                else it.toString()
            }
        }

//        Button CLick
        lifecycleScope.launchWhenStarted {
            prefManager.preferencesFlow.observe(this@MainActivity) { filterPreference ->
                searchPreference = filterPreference.searchPreference
            }
        }
        binding.settingDialog.setOnClickListener {
            openSettingDialog()
        }
    }

    private fun openSettingDialog() {
        val action = NavGraphDirections.actionGlobalSettingDialog(searchPreference)
        navController.navigate(action)
    }

    /**
     * @author Ayaan
     * @since 4.0.4
     */
    private fun showKeyboard() = binding.apply {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .apply {
                this.showSoftInput(binding.searchInput, 0)
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

    private fun openAboutUs() {
        val headerView = binding.navigationView.getHeaderView(0)
        val root = headerView.findViewById<RelativeLayout>(R.id.parent_layout)
        val button = headerView.findViewById<ImageButton>(R.id.button_about_us)
        root?.setOnClickListener {
            navigateToAboutUs()
        }
        button?.setOnClickListener {
            navigateToAboutUs()
        }

    }


    private fun navigateToAboutUs() {
        binding.drawer.closeDrawer(GravityCompat.START)
        setExitTransition()
        val directions = NavGraphDirections.actionGlobalAboutUsFragment()
        navController.navigate(directions)
    }


    private fun navigateToSearch() {
        getCurrentFragment()?.apply {
            exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
                duration = resources.getInteger(R.integer.duration_medium).toLong()
            }
            reenterTransition =
                MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
                    duration = resources.getInteger(R.integer.duration_medium).toLong()
                }
        }
        val action = NavGraphDirections.actionGlobalSearchFragment()
        navController.navigate(action)
    }

    override fun onBackPressed() {
        when {
            binding.drawer.isDrawerOpen(GravityCompat.START) -> {
                binding.drawer.closeDrawer(GravityCompat.START)
            }
            else -> super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun setDrawerEnabled(enabled: Boolean) {
        val lockMode =
            if (enabled) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        binding.drawer.setDrawerLockMode(lockMode)
    }


    /**
     * Check for version
     * @author aiyu
     * @since 4.0.1
     */
    private fun checkForUpdate() {
        appUpdateManager.registerListener {
            if (it.installStatus() == InstallStatus.DOWNLOADED) {
                showUpdateDownloadSnackBar()
            }
        }

        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && it.isUpdateTypeAllowed(
                    AppUpdateType.FLEXIBLE
                )
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    it, AppUpdateType.FLEXIBLE, this,
                    UPDATE_REQUEST_CODE
                )
            }
        }.addOnFailureListener {
            Log.e("Error on Update", "Failed to update ${it.message}")
        }
    }

    private fun showUpdateDownloadSnackBar() {
        binding.root.showSnackBar(
            resources.getString(R.string.update_downloaded),
            Snackbar.LENGTH_INDEFINITE,
            resources.getString(R.string.install),
        ) {
            appUpdateManager.completeUpdate()
        }
    }

    private fun getWarning() {
        val u = pref.getBoolean(KEY_FIRST_TIME_TOGGLE, false)
        db.collection("Utils")
            .document("Warning")
            .addSnapshotListener { value, _ ->
                val isEnable = value?.getBoolean("isEnable")
                val title = value?.getString("title")
                val link = value?.getString("link")
                val minVersion = value?.getDouble("minVersion")
                val isMinEdition =
                    BuildConfig.VERSION_CODE > (minVersion?.toInt() ?: INVALID_BUILD_VERSION)
                isEnable?.let { it ->
                    if (it && u && !isMinEdition)
                        link?.let { link ->
                            openWarningDialog(title ?: "", link)
                        }

                }
            }
    }

    private fun openWarningDialog(title: String, link: String) {
        try {
            val action = NavGraphDirections.actionGlobalWarningFragment(title, link)
            navController.navigate(action)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onMenuClick() {
        navigateToSearch()
    }


}
package com.atech.bit.ui.activities.main_activity

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.viewbinding.library.activity.viewBinding
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.atech.bit.BuildConfig
import com.atech.bit.NavGraphDirections
import com.atech.bit.R
import com.atech.bit.databinding.ActivityMainBinding
import com.atech.bit.ui.fragments.universal_dialog.UniversalDialogFragment
import com.atech.bit.utils.AttendanceUpload
import com.atech.bit.utils.AttendanceUploadDelegate
import com.atech.bit.utils.DrawerLocker
import com.atech.bit.utils.getVersion
import com.atech.bit.utils.onDestinationChange
import com.atech.bit.utils.openBugLink
import com.atech.bit.utils.openReleaseNotes
import com.atech.core.firebase.auth.AuthUseCases
import com.atech.core.firebase.remote.RemoteConfigHelper
import com.atech.core.room.attendance.AttendanceDao
import com.atech.core.utils.BitAppScope
import com.atech.core.utils.RemoteConfigKeys
import com.atech.core.utils.SharePrefKeys
import com.atech.core.utils.TAGS
import com.atech.core.utils.UPDATE_REQUEST_CODE
import com.atech.core.utils.isConnected
import com.atech.theme.Axis
import com.atech.theme.ParentActivity
import com.atech.theme.changeBottomNav
import com.atech.theme.changeStatusBarToolbarColorImageView
import com.atech.theme.currentNavigationFragment
import com.atech.theme.exitTransition
import com.atech.theme.isDark
import com.atech.theme.openCustomChromeTab
import com.atech.theme.openLinks
import com.atech.theme.openPlayStore
import com.atech.theme.setStatusBarUiTheme
import com.atech.theme.showSnackBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ParentActivity, DrawerLocker,
    AttendanceUpload by AttendanceUploadDelegate() {
    private val binding: ActivityMainBinding by viewBinding()

    @Inject
    lateinit var remoteConfigHelper: RemoteConfigHelper


    @Inject
    lateinit var pref: SharedPreferences

    @Inject
    lateinit var dao: AttendanceDao

    @Inject
    lateinit var auth: AuthUseCases

    @Inject
    lateinit var appUpdateManager: AppUpdateManager

    @BitAppScope
    @Inject
    lateinit var scope: CoroutineScope

    private var reviewInfo: ReviewInfo? = null
    private lateinit var reviewManager: ReviewManager
    private val viewModel: DataShareViewModel by viewModels()

    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
    }
    private val navController by lazy {
        navHostFragment.navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.apply {
            bottomNavigationSetup()
            handleDrawer()
            setHeader()
        }
        handleDestinationChange()
        shareReview()
        checkForUpdate()
        if (isConnected()) {
            fetchRemoteConfigData()
            if (auth.hasLogIn.invoke()) registerLifeCycleOwner(this@MainActivity)
        } else Log.d(TAGS.BIT_DEBUG.name, "onCreate: No Internet")
    }

    private fun ActivityMainBinding.bottomNavigationSetup() {
        bottomNavigation.setupWithNavController(navController)
        bottomNavigation.setOnItemSelectedListener {
            getCurrentFragment().apply {
                this?.exitTransition()
            }
            NavigationUI.onNavDestinationSelected(it, navController)
            true
        }
        bottomNavigation.setOnItemReselectedListener { }
    }

    private fun ActivityMainBinding.setHeader() = this.navigationView.apply {
        val headerView = getHeaderView(0)
        headerView.findViewById<TextView>(R.id.version).apply {
            text = resources.getString(
                com.atech.theme.R.string.full_version, getVersion()
            )
        }
    }

    private fun ActivityMainBinding.handleDrawer() = this.navigationView.apply {
        setupWithNavController(navController)
        setNavigationItemSelectedListener { menu ->
            setDrawerState(false)
            when (menu.itemId) {
                R.id.nav_connect -> resources.getString(com.atech.theme.R.string.instaLink)
                    .openLinks(this@MainActivity, com.atech.theme.R.string.no_intent_available)

                R.id.nav_mail -> this@MainActivity.openBugLink()
                R.id.nav_erp -> this@MainActivity.openCustomChromeTab(resources.getString(com.atech.theme.R.string.erp_link))
                R.id.nav_issue -> this@MainActivity.openCustomChromeTab(resources.getString(com.atech.theme.R.string.issue_link))
                R.id.nav_github -> this@MainActivity.openCustomChromeTab(resources.getString(com.atech.theme.R.string.github_link))
                R.id.nav_whats_new -> this@MainActivity.openReleaseNotes()
                R.id.nav_rate -> startReviewFlow()
                else -> NavigationUI.onNavDestinationSelected(menu, navController)
            }
            true
        }
    }

    private fun handleDestinationChange() {
        navController.onDestinationChange { destination ->
            when (destination.id) {
                in bottomNavigationFragment() -> getCurrentFragment().apply {
                    setDrawerEnabled(true)
                }

                else -> getCurrentFragment().apply {
                    setDrawerEnabled(false)
                }
            }
            //                Navigation bar visibility
            when (destination.id) {
                in baseFragments() -> {
                    setBottomNavigationVisibility(true)
                }

                else -> {
                    changeBottomNav(
                        android.viewbinding.library.R.attr.colorSurface
                    )
                    setBottomNavigationVisibility(false)
                }
            }
            when (destination.id) {
                in baseFragments() + fragmentWithBottomNavColor() -> changeBottomNav(
                    com.atech.theme.R.attr.bottomBar
                )

                else -> changeBottomNav(
                    android.viewbinding.library.R.attr.colorSurface
                )
            }
            when (destination.id) {
                com.atech.login.R.id.loginFragment -> changeStatusBarToolbarColorImageView(
                    MaterialColors.getColor(
                        this, com.atech.theme.R.attr.appLogoBackground, Color.WHITE
                    ).also {
                        setStatusBarUiTheme(this, false)
                    })

                in navigationViewFragments() -> changeStatusBarToolbarColorImageView(MaterialColors.getColor(
                    this, com.atech.theme.R.attr.bottomBar, Color.WHITE
                ).also {
                    setStatusBarUiTheme(this, !isDark())
                })

                in bottomSheetFragment() -> changeStatusBarToolbarColorImageView(MaterialColors.getColor(
                    this, com.atech.theme.R.attr.bottomSheetBackground, Color.WHITE
                ).also {
                    setStatusBarUiTheme(this, !isDark())
                })

                else -> changeStatusBarToolbarColorImageView(MaterialColors.getColor(
                    this, android.viewbinding.library.R.attr.colorSurface, Color.WHITE
                ).also {
                    setStatusBarUiTheme(this, !isDark())
                })
            }
        }
    }

    override fun setBottomNavigationVisibility(isVisible: Boolean) {
        binding.bottomNavigation.isVisible = isVisible
    }

    override fun getVersionName(): String {
        return BuildConfig.VERSION_NAME
    }

    override fun getNavigationFragmentId(): Int = R.id.fragment

    override fun getBottomNavigationFragment(): BottomNavigationView = binding.bottomNavigation

    override fun getHomeFragmentId(): Int = R.id.homeFragment

    override fun navigateToAboutUs(action: () -> Unit) {
        val headerView = binding.navigationView.getHeaderView(0)
        val root1 = headerView.findViewById<RelativeLayout>(R.id.parent_layout)
        val button = headerView.findViewById<ImageButton>(R.id.button_about_us)
        root1?.setOnClickListener {
            action.invoke()
        }
        button?.setOnClickListener {
            action.invoke()
        }
    }


    private fun getCurrentFragment(): Fragment? = supportFragmentManager.currentNavigationFragment
    private fun setExitTransition() = getCurrentFragment()?.exitTransition()

    private fun baseFragments() = listOf(
        R.id.homeFragment, com.atech.course.R.id.courseFragment
    )

    private fun fragmentWithBottomNavColor() = listOf(
        com.atech.attendance.R.id.attendanceFragment,
        R.id.holidayFragment,
        R.id.societyFragment,
        R.id.societyDetailFragment,
        R.id.eventFragment,
        R.id.eventDetailFragment,
        R.id.noticeFragment,
        R.id.noticeDetailFragment,
        R.id.libraryFragment,
        R.id.addEditLibraryFragment,
        R.id.cgpaCalculatorFragment,
        com.atech.login.R.id.loginFragment,
        R.id.aboutFragment,
        R.id.creditsFragment
    )

    private fun bottomNavigationFragment() = listOf(
        R.id.homeFragment,
        com.atech.course.R.id.courseFragment,
        com.atech.attendance.R.id.attendanceFragment
    )

    private fun navigationViewFragments() = listOf(
        R.id.holidayFragment,
        R.id.societyFragment,
        R.id.societyDetailFragment,
        R.id.administrationFragment,
        com.atech.course.R.id.semChooseFragment,
        com.atech.course.R.id.viewSyllabusFragment,
        R.id.eventFragment,
        R.id.eventDetailFragment,
        R.id.noticeFragment,
        R.id.noticeDetailFragment,
        R.id.libraryFragment,
        R.id.addEditLibraryFragment,
        R.id.cgpaCalculatorFragment,
        R.id.aboutFragment,
        R.id.detailDevFragment,
        R.id.creditsFragment
    )

    private fun bottomSheetFragment() = listOf(
        com.atech.attendance.R.id.addEditAttendanceBottomSheet,
        com.atech.attendance.R.id.detailViewBottomSheet,
        com.atech.attendance.R.id.archiveBottomSheet,
        com.atech.attendance.R.id.attendanceMenuBottomSheet,
        com.atech.attendance.R.id.addFromSyllabusBottomSheet,
        com.atech.login.R.id.chooseSemBottomSheet,
        R.id.editBottomSheet,
        R.id.chooseImageBottomSheet,
    )

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun setDrawerState(isOpen: Boolean) {
        if (isOpen) {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    override fun toggleDrawer() {
        binding.drawerLayout.apply {
            if (isDrawerOpen(GravityCompat.START)) {
                closeDrawer(GravityCompat.START)
            } else {
                openDrawer(GravityCompat.START)
            }
        }
    }

    override fun getDrawerLayout(): DrawerLayout {
        return binding.drawerLayout
    }

    override fun setDrawerEnabled(enabled: Boolean) {
        val lockMode =
            if (enabled) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        binding.drawerLayout.setDrawerLockMode(lockMode)
    }

    private fun shareReview() {
        reviewManager = ReviewManagerFactory.create(this)
        val managerInfoTask = reviewManager.requestReviewFlow()
        managerInfoTask.addOnCompleteListener { task ->
            if (task.isSuccessful) reviewInfo = task.result
            else Log.e(TAGS.BIT_ERROR.name, "shareReview:  Can't open review manager")
        }
    }

    private fun startReviewFlow() {
        if (reviewInfo != null) reviewManager.launchReviewFlow(this, reviewInfo!!)
            .addOnCompleteListener {
                Toast.makeText(this, "Review is completed", Toast.LENGTH_SHORT).show()
            }
        else openPlayStore(packageName)
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
                    it, AppUpdateType.FLEXIBLE, this, UPDATE_REQUEST_CODE
                )
            }
        }.addOnFailureListener {
            Log.e("Error on Update", "Failed to update ${it.message}")
        }
    }

    private fun showUpdateDownloadSnackBar() {
        binding.root.showSnackBar(
            resources.getString(com.atech.theme.R.string.update_downloaded),
            Snackbar.LENGTH_INDEFINITE,
            resources.getString(com.atech.theme.R.string.install),
        ) {
            appUpdateManager.completeUpdate()
        }
    }

    //    --------------------------------- Remote Config ----------------------------------
    private fun fetchRemoteConfigData() {
        remoteConfigHelper.fetchData(failure = {
            Log.e(TAGS.BIT_ERROR.name, "fetchRemoteConfigData: + $it")
        }) {
            remoteConfigHelper.getString(RemoteConfigKeys.KEY_TOGGLE_SYLLABUS_SOURCE_ARRAY.name)
                .let {
                    pref.edit().putString(SharePrefKeys.KeyToggleSyllabusSource.name, it).apply()
                }
            remoteConfigHelper.getString(RemoteConfigKeys.SYLLABUS_VISIBILITY.name).let {
                pref.edit().putString(SharePrefKeys.SyllabusVisibility.name, it).apply()
            }

            pref.edit().apply {
                putInt(
                    SharePrefKeys.ShowTimes.name,
                    remoteConfigHelper.getLong(RemoteConfigKeys.show_times.name).toInt()
                )
            }.apply()

            val minVersion =
                remoteConfigHelper.getLong(RemoteConfigKeys.ann_version.name).toInt().also {
                    pref.getInt(SharePrefKeys.KeyAnnVersion.name, 0).let {
                        if (it == 0) {
                            pref.edit().putInt(SharePrefKeys.KeyAnnVersion.name, it).apply()
                        }
                    }
                }
            val annTitle = remoteConfigHelper.getString(RemoteConfigKeys.ann_title.name)

            val currentAnnVersion = pref.getInt(SharePrefKeys.KeyAnnVersion.name, 0)
            if (minVersion != currentAnnVersion && minVersion != 1) {
                pref.edit().putInt(SharePrefKeys.CurrentShowTime.name, 1).apply()
                pref.edit().putInt(SharePrefKeys.KeyAnnVersion.name, minVersion).apply()
            }

            getWarningScreenDetails(remoteConfigHelper)

            val annMessage = remoteConfigHelper.getString(RemoteConfigKeys.ann_message.name)
            val annLink = remoteConfigHelper.getString(RemoteConfigKeys.ann_link.name)
            val annPosButton = remoteConfigHelper.getString(RemoteConfigKeys.ann_pos_button.name)
            val annNegButton = remoteConfigHelper.getString(RemoteConfigKeys.ann_neg_button.name)
            val universalDialogData = UniversalDialogFragment.UniversalDialogData(
                title = annTitle,
                message = annMessage,
                link = annLink,
                positiveButtonText = annPosButton,
                negativeButtonText = annNegButton
            )
            viewModel.setUniversalDialogData(universalDialogData, minVersion)
            getInstances(
                dao,
                auth,
                pref,
                remoteConfigHelper.getLong(RemoteConfigKeys.MAX_TIMES_UPLOAD.name).toInt(),
                scope
            )
        }
    }

    private fun getWarningScreenDetails(remoteConfigUtil: RemoteConfigHelper) {
        val isSetUpDone = pref.getBoolean(SharePrefKeys.SetUpDone.name, false)
        val isEnable = remoteConfigUtil.getBoolean(RemoteConfigKeys.isEnable.name)
        val title = remoteConfigUtil.getString(RemoteConfigKeys.title.name)
        val link = remoteConfigUtil.getString(RemoteConfigKeys.link.name)
        val minVersion = remoteConfigUtil.getLong(RemoteConfigKeys.minVersion.name).toInt()
        val buttonText = remoteConfigUtil.getString(RemoteConfigKeys.button_text.name)
        val isMinEdition = BuildConfig.VERSION_CODE > minVersion
        Log.d(
            TAGS.BIT_REMOTE.name,
            "getWarning: $isEnable, $title, $link, $minVersion, $buttonText, $isMinEdition"
        )
        Log.d(TAGS.BIT_REMOTE.name, "getWarning $isSetUpDone , $isEnable , $isMinEdition")
        if (!isMinEdition && (isEnable && isSetUpDone)) {
            openWarningDialog(title, link, buttonText)
        }
    }

    private fun openWarningDialog(title: String, link: String, buttonText: String) {
        getCurrentFragment().apply {
            this?.exitTransition(Axis.Z)
        }
        try {
            val action = NavGraphDirections.actionGlobalWarningFragment(title, link, buttonText)
            navController.navigate(action)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
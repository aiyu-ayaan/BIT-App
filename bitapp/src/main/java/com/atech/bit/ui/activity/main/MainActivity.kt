package com.atech.bit.ui.activity.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import com.atech.bit.ui.navigation.ParentScreenRoutes
import com.atech.bit.ui.navigation.TopLevelNavigationGraph
import com.atech.bit.ui.navigation.TopLevelRoute
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.utils.AttendanceUpload
import com.atech.bit.utils.AttendanceUploadDelegate
import com.atech.core.utils.isConnected
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class MainActivity : ComponentActivity(), LifecycleEventObserver,
    AttendanceUpload by AttendanceUploadDelegate() {

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var appUpdateManager: AppUpdateManager

    private val updateType = AppUpdateType.FLEXIBLE


    private val result =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            // handle callback
            if (result.resultCode != RESULT_OK) {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
            } else
                Toast.makeText(this, "Update success", Toast.LENGTH_SHORT).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeState by viewModel.themeState
            val navHostController = rememberNavController()
            BITAppTheme(
                dynamicColor = themeState.isDynamicColorActive,
                darkTheme = when (themeState.isDarkTheme) {
                    ThemeMode.LIGHT -> false
                    ThemeMode.DARK -> true
                    ThemeMode.SYSTEM -> isSystemInDarkTheme()
                }
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface
                ) {
                    viewModel.action = {
                        navHostController.navigate(
                            TopLevelRoute.LOGIN.route
                        )
                    }
                    TopLevelNavigationGraph(
                        navHostController = navHostController,
                        communicatorViewModel = viewModel,
                        startDestination = if (viewModel.isForceScreenEnable.value) ParentScreenRoutes.ForceScreen.route
                        else if (viewModel.hasSetUpDone) TopLevelRoute.MAIN_SCREEN.route
                        else TopLevelRoute.LOGIN.route
                    )
                }
            }
        }
        if (isConnected()) {
            observeRemoteData()
            if (viewModel.checkHasLogIn())
                registerLifeCycleOwner(this)
        }

        if (viewModel.hasSetUpDone) {
            checkForUpdate()
            updateAppFlex()
        }
        lifecycle.addObserver(this)
    }

    private fun observeRemoteData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fetchRemoteConfigDetails(
                    ::getInstances
                )
            }
        }
    }

    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            installApp()
        }
    }

    private fun installApp() {
        Toast.makeText(
            this,
            "Download successful.Restart app in 5 seconds.",
            Toast.LENGTH_SHORT
        ).show()
        lifecycleScope.launch {
            delay(5.seconds)
            appUpdateManager.completeUpdate()
        }
    }

    private fun updateAppFlex() {
        appUpdateManager.registerListener(installStateUpdatedListener)
    }

    private fun checkForUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && it.isUpdateTypeAllowed(updateType)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    it,
                    result,
                    AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE)
                        .setAllowAssetPackDeletion(true)
                        .build()
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    installApp()
                }
            }
    }


    override fun onDestroy() {
        super.onDestroy()
        appUpdateManager.unregisterListener(installStateUpdatedListener)
    }


    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            viewModel.onEvent(MainViewModel.SharedEvents.ToggleDrawer(null))
        }
    }
}

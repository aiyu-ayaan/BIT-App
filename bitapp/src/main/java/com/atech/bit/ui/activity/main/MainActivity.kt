package com.atech.bit.ui.activity.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.chat.compose.ChatScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity(), LifecycleEventObserver {

    private val viewModel: MainViewModel by viewModels()
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
//                    viewModel.action = {
//                        navHostController.navigate(
//                            TopLevelRoute.LOGIN.route
//                        )
//                    }
//                    TopLevelNavigationGraph(
//                        navHostController = navHostController,
//                        communicatorViewModel = viewModel,
//                        startDestination =
//                        if (viewModel.isForceScreenEnable.value)
//                            ParentScreenRoutes.ForceScreen.route
//                        else
//                            if (viewModel.hasSetUpDone) TopLevelRoute.MAIN_SCREEN.route
//                            else TopLevelRoute.LOGIN.route
//                    )
                    ChatScreen()
                }
            }
        }
        observeRemoteData()
        lifecycle.addObserver(this)
    }

    private fun observeRemoteData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fetchRemoteConfigDetails()
            }
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            viewModel.onEvent(MainViewModel.SharedEvents.ToggleDrawer(null))
        }
    }
}

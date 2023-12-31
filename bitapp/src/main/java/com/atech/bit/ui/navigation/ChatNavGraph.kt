package com.atech.bit.ui.navigation

import androidx.compose.runtime.collectAsState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.atech.bit.ui.activity.main.MainViewModel
import com.atech.bit.utils.animatedComposable
import com.atech.bit.utils.fadeThroughComposable
import com.atech.bit.utils.sharedViewModel
import com.atech.chat.ChatViewModel
import com.atech.chat.compose.ChatScreen
import com.atech.chat.compose.setting.ChatSettingsScreen
import com.atech.chat.compose.setting.settingScreenRouteName
import com.atech.core.utils.connectivity.ConnectivityObserver


sealed class ChatScreens(val route: String) {
    data object ChatScreen : ChatScreens("chat_list")
    data object ChatSettingScreen : ChatScreens(settingScreenRouteName)
}

fun NavGraphBuilder.chatNavGraph(
    navHostController: NavHostController,
    communicatorViewModel: MainViewModel
) {
    navigation(
        startDestination = ChatScreens.ChatScreen.route, route = RouteName.CHAT.value
    ) {
        fadeThroughComposable(
            route = ChatScreens.ChatScreen.route
        ) {
            val viewModel = it.sharedViewModel<ChatViewModel>(navController = navHostController)
            ChatScreen(
                navController = navHostController,
                wrapLine = viewModel.chatSettingUi.value.isWrapWord,
                isConnected = viewModel.connectivity.collectAsState(initial = ConnectivityObserver.Status.Unavailable).value,
                chatUiState = viewModel.uiState.value,
                isLoading = viewModel.isLoading.value,
                keepChat = viewModel.chatSettingUi.value.isKeepChat,
                hasLogIn = viewModel.hasLogIn.invoke(),
                chanceWithMax = communicatorViewModel.chanceWithMax.value,
                onEvent = viewModel::onEvent
            )
        }
        animatedComposable(
            route = ChatScreens.ChatSettingScreen.route
        ) {
            val viewModel = it.sharedViewModel<ChatViewModel>(navController = navHostController)
            ChatSettingsScreen(
                navController = navHostController,
                uiState = viewModel.chatSettingUi.value,
                onEvent = viewModel::onChatSettingEvents
            )
        }
    }
}
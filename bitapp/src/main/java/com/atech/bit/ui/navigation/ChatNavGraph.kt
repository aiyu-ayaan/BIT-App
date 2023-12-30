package com.atech.bit.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.atech.bit.utils.fadeThroughComposable
import com.atech.bit.utils.sharedViewModel
import com.atech.chat.ChatViewModel
import com.atech.chat.compose.ChatScreen
import com.atech.chat.compose.setting.ChatSettingsScreen
import com.atech.chat.compose.setting.settingScreenRouteName


sealed class ChatScreens(val route: String) {
    data object ChatScreen : ChatScreens("chat_list")
    data object ChatSettingScreen : ChatScreens(settingScreenRouteName)
}

fun NavGraphBuilder.chatNavGraph(
    navHostController: NavHostController
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
                viewModel = viewModel,
                wrapLine = viewModel.chatSettingUi.value.isWrapWord
            )
        }
        fadeThroughComposable(
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
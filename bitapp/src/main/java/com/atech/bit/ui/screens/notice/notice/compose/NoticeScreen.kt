/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.notice.notice.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.atech.bit.R
import com.atech.bit.ui.comman.BackToolbar
import com.atech.bit.ui.comman.GlobalEmptyScreen
import com.atech.bit.ui.comman.NoticeItem
import com.atech.bit.ui.navigation.DeepLinkRoutes
import com.atech.bit.ui.navigation.NoticeScreenRoute
import com.atech.bit.ui.navigation.navigateWithDeepLink
import com.atech.bit.ui.screens.notice.NoticeScreenEvent
import com.atech.bit.ui.screens.notice.NoticeViewModel
import com.atech.bit.ui.theme.BITAppTheme
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NoticeScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: NoticeViewModel = hiltViewModel()
) {
    val fetchedNotice by viewModel.fetchNotice
    Scaffold(
        modifier = modifier,
        topBar = {
            BackToolbar(
                title = R.string.notice,
                onNavigationClick = {
                    navController.navigateUp()
                }
            )
        }
    ) {
        GlobalEmptyScreen(
            modifier = Modifier.padding(it),
            isEmpty = fetchedNotice.isEmpty(),
            emptyText = "No notice found",
            content = {
                LazyColumn(
                    modifier = Modifier
                        .consumeWindowInsets(it),
                    contentPadding = it
                ) {
                    items(
                        items = fetchedNotice,
                        key = { UUID.randomUUID() }
                    ) { notice ->
                        NoticeItem(
                            model = notice,
                            modifier = Modifier.animateItem(),
                            getAttach = viewModel.getAttach,
                            onNoticeClick = { noticeModel ->
                                viewModel.onEvent(
                                    NoticeScreenEvent
                                        .OnEventClick(
                                            noticeModel
                                        )
                                )
                                navController.navigate(
                                    NoticeScreenRoute
                                        .NoticeDetailsScreen.route
                                )
                            },
                            onClick = {
                                navController.navigateWithDeepLink(
                                    DeepLinkRoutes.ViewImageRoute(it)
                                )
                            }
                        )
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NoticeScreenPreview() {
    BITAppTheme {
        NoticeScreen()
    }
}
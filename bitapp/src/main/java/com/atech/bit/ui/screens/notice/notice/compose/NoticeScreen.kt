/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.notice.notice.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
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
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.grid_0_5
import com.atech.core.datasource.firebase.firestore.NoticeModel
import com.atech.core.datasource.retrofit.model.CollegeNotice
import com.atech.core.usecase.GetAttach
import com.atech.core.utils.NOTICE_SITE_LINK
import com.atech.core.utils.openLinks
import kotlinx.coroutines.launch
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoticeScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    fetchNotice: List<NoticeModel> = emptyList(),
    fetchCollegeNotice: List<CollegeNotice> = emptyList(),
    getAttach: GetAttach? = null,
    onEvent: (NoticeScreenEvent) -> Unit = {}
) {
    val tabItems = listOf(
        "College",
        "App"
    )
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val pagerState = rememberPagerState {
        tabItems.size
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            BackToolbar(
                title = R.string.notice,
                onNavigationClick = {
                    navController.navigateUp()
                },
                actions = {
                    AnimatedVisibility(selectedTabIndex == 0) {
                        IconButton(onClick = {
                            scope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    "Notices are fetch from official BIT Mesra Site",
                                    duration = SnackbarDuration.Long,
                                    actionLabel = "Open",
                                )
                                when (result) {
                                    SnackbarResult.Dismissed -> {}
                                    SnackbarResult.ActionPerformed -> {
                                        NOTICE_SITE_LINK.openLinks(context)
                                    }
                                }
                            }
                        }) {
                            Icon(imageVector = Icons.Rounded.Info, contentDescription = "info")
                        }
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            LaunchedEffect(selectedTabIndex) {
                pagerState.animateScrollToPage(selectedTabIndex)
            }
            LaunchedEffect(pagerState.currentPage) {
                selectedTabIndex = pagerState.currentPage
            }
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabItems.forEachIndexed { index, s ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                        },
                        text = {
                            Text(s)
                        }
                    )
                }
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { index ->
                when (index) {
                    0 -> CollegeNotifications(
                        fetchCollegeNotice = fetchCollegeNotice
                    )

                    1 -> AppNotifications(
                        fetchedNotice = fetchNotice,
                        navController = navController,
                        getAttach = getAttach,
                        onEvent = onEvent
                    )
                }
            }
        }
    }
}

@Composable
private fun CollegeNotifications(
    modifier: Modifier = Modifier,
    fetchCollegeNotice: List<CollegeNotice>
) {
    val context = LocalContext.current
    GlobalEmptyScreen(
        modifier = modifier,
        isEmpty = fetchCollegeNotice.isEmpty(),
        emptyText = "No College notice found"
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(
                grid_0_5
            )
        ) {
            items(fetchCollegeNotice) {
                CollegeNoticeItem(
                    modifier = Modifier
                        .animateItem(),
                    model = it,
                    onClick = {
                        it.link.openLinks(context)
                    }
                )
            }
        }
    }
}

@Composable
private fun AppNotifications(
    fetchedNotice: List<NoticeModel>,
    navController: NavController,
    getAttach: GetAttach?,
    onEvent: (NoticeScreenEvent) -> Unit
) {
    GlobalEmptyScreen(
        modifier = Modifier,
        isEmpty = fetchedNotice.isEmpty(),
        emptyText = "No notice found",
        content = {
            LazyColumn(
                modifier = Modifier,
            ) {
                items(
                    items = fetchedNotice,
                    key = { UUID.randomUUID() }
                ) { notice ->
                    NoticeItem(
                        model = notice,
                        modifier = Modifier.animateItem(),
                        getAttach = getAttach ?: return@items,
                        onNoticeClick = { noticeModel ->
                            onEvent(
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


@Preview(showBackground = true)
@Composable
private fun NoticeScreenPreview() {
    BITAppTheme {
        NoticeScreen()
    }
}
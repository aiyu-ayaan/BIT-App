/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.society.components.detail

import android.content.Context
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.atech.bit.R
import com.atech.bit.ui.activity.main.MainActivity
import com.atech.bit.ui.comman.BackToolbar
import com.atech.bit.ui.comman.BottomPadding
import com.atech.bit.ui.comman.ImageIconButton
import com.atech.bit.ui.comman.ImageLoader
import com.atech.bit.ui.comman.singleElement
import com.atech.bit.ui.screens.society.SocietyViewModel
import com.atech.bit.ui.theme.grid_1
import com.atech.bit.utils.hexToRgb
import com.atech.core.utils.openLinks


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocietyDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: SocietyViewModel = hiltViewModel(),
    navController: NavController
) {
    val societyDetail = viewModel.currentClickSociety.value
    val toolbarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val context = LocalContext.current
    var webView: WebView? = remember {
        null
    }
    BackHandler {
        navigateUp(webView, navController)
    }

    Scaffold(modifier = modifier, topBar = {
        BackToolbar(title = societyDetail.name,
            scrollBehavior = toolbarScrollBehavior,
            onNavigationClick = {
                navigateUp(webView, navController)
            },
            actions = {
                ImageIconButton(
                    icon = R.drawable.ic_instagram,
                    tint = MaterialTheme.colorScheme.primary
                ) {
                    if (societyDetail.ins.isNotBlank())
                        societyDetail.ins
                            .openLinks(
                                context
                            )
                }
            })
    }) {
        val onSurface = MaterialTheme.colorScheme.onSurface
        val surface = MaterialTheme.colorScheme.surface
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .nestedScroll(toolbarScrollBehavior.nestedScrollConnection)
        ) {
            singleElement(
                key = "Image"
            ) {
                ImageLoader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp), imageUrl = societyDetail.logo
                )
                Spacer(modifier = Modifier.height(grid_1))
            }
            singleElement(
                key = "Android View"
            ) {
                AndroidView(modifier = Modifier, factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        fitsSystemWindows = true
                        setInitialScale(getScaleSociety(context = context))
                        isScrollContainer = false
                        isFocusable = false;
                        setFocusableInTouchMode(false);
                    }.also { webView = it }
                }, update = { webView ->
                    val body = """
                <html>
                <head>
                <meta name="color-scheme" content="dark light">
                <style>
                body{
                    text-align:justify;
                    background-color:${surface.hexToRgb()};
                    color:${onSurface.hexToRgb()};
                 }
                 tr{
                    color:rgb(0,0,0);
                 }
                 </style>
                </head>
                <body>
                ${societyDetail.des}
                </body>
                </html>
            """.trimIndent()
                    webView.loadData(
                        body, "text/html; charset=utf-8", "utf-8"
                    )
                })
                BottomPadding()
            }
        }
    }
}

private fun navigateUp(
    webView: WebView?,
    navController: NavController
) {
    if (webView?.canGoBack() == true)
        webView.goBack()
    else {
        webView?.destroy()
        navController.navigateUp()
    }
}

@Suppress("DEPRECATION")
fun getScaleSociety(contentWidth: Double = 400.0, context: Context): Int {
    try {
        val activity = context as MainActivity
        return run {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val `val` = displayMetrics.widthPixels / contentWidth * 100.0
            `val`.toInt()
        }
    } catch (e: Exception) {
        return 100
    }
}
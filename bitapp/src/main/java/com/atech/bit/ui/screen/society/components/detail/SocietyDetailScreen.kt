package com.atech.bit.ui.screen.society.components.detail

import android.content.Context
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.webkit.WebView
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.atech.bit.ui.activity.main.MainActivity
import com.atech.bit.ui.screen.society.SocietyViewModel
import com.atech.components.BackToolbar
import com.atech.components.BottomPadding
import com.atech.components.ImageIconButton
import com.atech.components.ImageLoader
import com.atech.components.singleElement
import com.atech.theme.grid_1
import com.atech.utils.hexToRgb
import com.atech.utils.openLinks


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
    Scaffold(modifier = modifier, topBar = {
        BackToolbar(title = societyDetail.name,
            scrollBehavior = toolbarScrollBehavior,
            onNavigationClick = {
                navController.navigateUp()
            },
            actions = {
                ImageIconButton(
                    icon = com.atech.theme.R.drawable.ic_instagram,
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
                    }
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
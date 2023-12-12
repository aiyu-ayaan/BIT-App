package com.atech.course.screen.sub_view

import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.atech.components.BackToolbar
import com.atech.syllabus.getFragment
import com.atech.theme.BITAppTheme
import com.atech.utils.hexToRgb
import com.mukesh.MarkDown
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewSubjectScreen(
    modifier: Modifier = Modifier,
    viewModel: ViewSubjectViewModel = hiltViewModel(),
    navController: NavController = rememberNavController()
) {

    val toolbarScroll = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scrollState = rememberScrollState()
    val data = viewModel.onlineMdContent.value
    val isOnline = viewModel.isOnline
    var isComposeViewVisible by rememberSaveable {
        mutableStateOf(false)
    }
    val courseSem = viewModel.courseSem
    LaunchedEffect(key1 = true) {
        delay(500)
        isComposeViewVisible = true
    }
    BackHandler {
        isComposeViewVisible = false
        navController.navigateUp()
    }

    BITAppTheme(
        dynamicColor = isOnline
    ) {
        Scaffold(topBar = {
            BackToolbar(
                title = "", onNavigationClick = {
                    isComposeViewVisible = false
                    navController.navigateUp()
                }, scrollBehavior = toolbarScroll
            )
        }) {
            Column(
                modifier = modifier
                    .nestedScroll(toolbarScroll.nestedScrollConnection)
                    .verticalScroll(scrollState)
                    .padding(it)
                    .background(
                        MaterialTheme.colorScheme.surface
                    )
            ) {
                if (isOnline) {
                    if (isComposeViewVisible) LoadMarkDown(
                        data = data
                    )
                } else LoadSyllabusFromXml(res = courseSem)
            }
        }
    }
}

@Composable
fun LoadSyllabusFromXml(res: String) {
    AndroidView(
        factory = {
            View.inflate(
                it,
                getFragment(res),
                null
            )
        },
        modifier = Modifier.fillMaxSize()
    )
}


@Composable
fun LoadMarkDown(
    modifier: Modifier = Modifier, data: String = ""
) {
    val d = data + "<br> <br><style> body{background-color: ${
        MaterialTheme.colorScheme.surface.hexToRgb()
    } ; color:${MaterialTheme.colorScheme.onSurface.hexToRgb()};}</style>"
    MarkDown(
        text = d, modifier = modifier.fillMaxSize()
    )
}

@Preview(showBackground = true)
@Composable
fun ViewSubjectScreenPreview() {
    BITAppTheme {
        LoadMarkDown()
    }
}


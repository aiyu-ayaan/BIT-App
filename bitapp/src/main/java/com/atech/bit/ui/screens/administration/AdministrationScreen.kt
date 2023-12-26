package com.atech.bit.ui.screens.administration

import android.view.View
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.atech.bit.R
import com.atech.bit.ui.comman.BackToolbar
import com.atech.bit.ui.comman.StateLoadingScreen
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.utils.OnErrorEvent
import com.atech.bit.utils.hexToRgb
import com.mukesh.MarkdownView
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdministrationScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: AdministrationViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val data = viewModel.date.value

    var hasError by remember {
        mutableStateOf(false to "")
    }

    LaunchedEffect(key1 = true) {
        viewModel.oneTimeEvent.collectLatest {
            when (it) {
                is OnErrorEvent.OnError -> {
                    hasError = true to it.message
                }
            }
        }
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            BackToolbar(
                title = R.string.administration,
                onNavigationClick = {
                    navController.navigateUp()
                }
            )
        }
    ) {
        if (data.isEmpty()) {
            StateLoadingScreen(
                modifier = Modifier.fillMaxSize(),
                isLoading = data.isEmpty() && !hasError.first,
                isHasError = hasError.first,
                errorMessage = "Unable to load data \n Error : ${hasError.second}"
            )
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(scrollState)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            val d = data + "<br> <br><style> body{background-color: ${
                MaterialTheme.colorScheme.surface.hexToRgb()
            } ; color:${MaterialTheme.colorScheme.onSurface.hexToRgb()};}</style>"
            AndroidView(factory = { context ->
                View.inflate(
                    context,
                    R.layout.layout_markdown,
                    null
                )
            },
                modifier = modifier.fillMaxSize(),
                update = { view ->
                    view.findViewById<MarkdownView>(R.id.markdown).setMarkDownText(d)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdministrationScreenPreview() {
    BITAppTheme {
        AdministrationScreen()
    }
}
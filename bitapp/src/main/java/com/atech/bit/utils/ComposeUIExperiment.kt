package com.atech.bit.utils

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.themeadapter.material3.Mdc3Theme

@Composable
fun ComposeUIExperiment() {
}


@Composable
@Preview
fun ComposeUIExperimentPreview() {
    Mdc3Theme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ComposeUIExperimentPreview()
        }
    }
}
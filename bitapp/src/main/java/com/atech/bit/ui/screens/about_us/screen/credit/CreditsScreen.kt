package com.atech.bit.ui.screens.about_us.screen.credit

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.atech.bit.ui.comman.BackToolbar
import com.atech.bit.ui.comman.singleElement
import com.atech.bit.ui.theme.BITAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditsScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController()
) {
    Scaffold(
        modifier = Modifier,
        topBar = {
            BackToolbar(
                title = "Title",
                onNavigationClick = {
                    navController.popBackStack()
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .consumeWindowInsets(it),
            contentPadding = it
        ) {
            singleElement("Dev Details") {

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun DevDetailsScreenPreview() {
    BITAppTheme {
    }
}
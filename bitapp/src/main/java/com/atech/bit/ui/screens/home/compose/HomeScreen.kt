package com.atech.bit.ui.screens.home.compose

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.atech.bit.ui.activity.MainViewModel
import com.atech.bit.ui.activity.toggleDrawer
import com.atech.bit.ui.theme.BITAppTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    communicatorViewModel: MainViewModel
) {
        val isSearchBarActive = communicatorViewModel.isSearchActive.value
    Scaffold(
        modifier = modifier,
        topBar = {
            var query by remember { mutableStateOf("") }
            SearchToolBar(
                query = query,
                onQueryChange = { query = it },
                active = false,
                 onActiveChange = { communicatorViewModel.onEvent(MainViewModel.SharedEvents.ToggleSearchActive) },
                 onTrailingIconClick = {
                     communicatorViewModel.onEvent(MainViewModel.SharedEvents.ToggleSearchActive)
                 },
                 onLeadingIconClick = {
                     communicatorViewModel.onEvent(
                         MainViewModel.SharedEvents.ToggleDrawer(
                             toggleDrawer(communicatorViewModel)
                         )
                     )
                 }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.consumeWindowInsets(it),
            contentPadding = it
        ) {
            items(1) {
                Text(text = "Home")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    BITAppTheme {

    }
}
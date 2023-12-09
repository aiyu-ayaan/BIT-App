package com.atech.bit.ui.screen.home.compose

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
import com.atech.theme.BITAppTheme
import com.atech.view_model.SharedViewModel
import com.atech.view_model.SharedEvents

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    communicatorViewModel: SharedViewModel
) {
    val isSearchBarActive = communicatorViewModel.isSearchActive.value
    Scaffold(
        modifier = modifier,
        topBar = {
            var query by remember { mutableStateOf("") }
            SearchToolBar(
                query = query,
                onQueryChange = { query = it },
                active = isSearchBarActive,
                onActiveChange = { communicatorViewModel.onEvent(SharedEvents.ToggleSearchActive) },
                onTrailingIconClick = {
                    communicatorViewModel.onEvent(SharedEvents.ToggleSearchActive)
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
package com.atech.bit.ui.fragments.search.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.atech.bit.ui.fragments.search.SearchEvent
import com.atech.bit.ui.fragments.search.SearchViewModel
import com.google.accompanist.themeadapter.material3.Mdc3Theme


@Composable
fun SearchScreen(
    modifier: Modifier = Modifier, searchViewModel: SearchViewModel = hiltViewModel()
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        SearchBarComponent(state = searchViewModel.searchTitle.value, onValueChange = {
            searchViewModel.setEvent(SearchEvent.EnteredTitle(it))
        }, onFocusChange = {
            searchViewModel.setEvent(SearchEvent.ChangeTitleFocus(it))
        })
    }
}


@Preview(name = "Light Mode")
@Composable
fun SearchScreenPreview() {
    Mdc3Theme {
        Surface(modifier = Modifier.fillMaxSize()) {
            SearchScreen(modifier = Modifier)
        }
    }
}

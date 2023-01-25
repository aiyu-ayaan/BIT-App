package com.atech.bit.ui.fragments.search.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.atech.bit.ui.fragments.search.SearchEvent
import com.atech.bit.ui.fragments.search.SearchViewModel
import com.atech.bit.utils.DPS.GRID_1
import com.atech.bit.utils.DPS.GRID_2
import com.atech.core.data.room.syllabus.SyllabusModel
import com.google.accompanist.themeadapter.material3.Mdc3Theme


@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel = hiltViewModel(),
    onClick: (SyllabusModel) -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        SearchBarComponent(state = searchViewModel.searchTitle.value, onValueChange = {
            searchViewModel.setEvent(SearchEvent.EnteredTitle(it))
        }, onFocusChange = {
            searchViewModel.setEvent(SearchEvent.ChangeTitleFocus(it))
        })
        SearchSyllabusComponent(searchViewModel, onClick)
    }
}

@Composable
private fun SearchSyllabusComponent(
    searchViewModel: SearchViewModel,
    onClick: (SyllabusModel) -> Unit = {}
) {
    Spacer(modifier = Modifier.height(GRID_1))
    if (searchViewModel.searchSyllabus.value.isEmpty()
        || searchViewModel.searchTitle.value.text.isEmpty()
    ) {
        LandingIcon(
            state = searchViewModel.searchTitle.value.isHintVisible
                    || searchViewModel.searchTitle.value.text.isEmpty()
        )
    } else {
        SearchedContent(
            modifier = Modifier.padding(horizontal = GRID_2),
            state = searchViewModel.searchSyllabus.value,
            onClick = onClick
        )
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

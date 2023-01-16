package com.atech.bit.ui.fragments.search.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.atech.bit.R
import com.google.accompanist.themeadapter.material3.Mdc3Theme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Icon(painter = painterResource(id = R.drawable.ic_search), contentDescription = null)
            Text(text = "Search")
        }
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

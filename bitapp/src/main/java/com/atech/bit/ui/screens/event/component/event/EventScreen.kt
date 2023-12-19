package com.atech.bit.ui.screens.event.component.event

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.atech.bit.R
import com.atech.bit.ui.comman.BackToolbar
import com.atech.bit.ui.navigation.EventRoute
import com.atech.bit.ui.screens.event.EventScreenEvent
import com.atech.bit.ui.screens.event.EventViewModel
import com.atech.bit.ui.theme.BITAppTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: EventViewModel = hiltViewModel()
) {
    val events by viewModel.fetchEvents

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            BackToolbar(
                title = R.string.events,
                modifier = modifier,
                onNavigationClick = {
                    navController.navigateUp()
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .consumeWindowInsets(it),
            contentPadding = it
        ) {
            items(
                items =events,
                key = { event -> event.title + event.created }
            ) { model ->
                EventItem(
                    model = model,
                    onEventClick = { clickItems ->
                        viewModel.onEvent(EventScreenEvent.OnEventClick(clickItems))
                        navController.navigate(EventRoute.DetailScreen.route)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EventScreenPreview() {
    BITAppTheme {
        EventScreen()
    }
}
package com.atech.bit.ui.screen.holiday.compose

import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.atech.bit.ui.screen.holiday.HolidayViewModel
import com.atech.components.BackToolbar
import com.atech.components.singleElement
import com.atech.theme.BITAppTheme
import com.atech.theme.grid_1

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun HolidayScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: HolidayViewModel = hiltViewModel()
) {
    val scrollState = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var selectedIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    val holiday = viewModel.holidays.value
    Scaffold(
        modifier = modifier,
        topBar = {
            BackToolbar(
                title = "Holiday",
                onNavigationClick = {
                    navController.navigateUp()
                },
                scrollBehavior = scrollState
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .consumeWindowInsets(it)
                .nestedScroll(scrollState.nestedScrollConnection),
            contentPadding = it
        ) {
            singleElement(key = "TabView") {
                HolidayTabLayout(
                    selectedTabIndex = selectedIndex,
                    onTabSelected = { index ->
                        selectedIndex = index
                        viewModel.onEvent(index)
                    }
                )
            }

            items(items = holiday, key = { it1 -> it1.hashCode() }) { holiday ->
                HolidayItem(
                    holiday = holiday,
                    modifier = Modifier.animateItemPlacement(
                        animationSpec = spring(
                            dampingRatio = 2f, stiffness = 600f
                        )
                    )
                )
            }
        }
    }
}

@Composable
fun HolidayTabLayout(
    modifier: Modifier = Modifier,
    selectedTabIndex: Int = 0,
    onTabSelected: (Int) -> Unit = {}
) {
    TabRow(
        modifier = modifier,
        selectedTabIndex = selectedTabIndex
    ) {
        listOf("Main", "Res").forEachIndexed { index, s ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) }
            ) {
                Text(
                    text = s,
                    modifier = Modifier
                        .padding(grid_1)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HolidayScreenPreview() {
    BITAppTheme {
        HolidayScreen()
    }
}
package com.atech.course.screen.course

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.atech.components.Toolbar
import com.atech.components.singleElement
import com.atech.course.CourseEvents
import com.atech.course.CourseScreenRoute
import com.atech.course.CourseViewModel
import com.atech.course.R
import com.atech.course.components.CourseItem
import com.atech.theme.BITAppTheme
import com.atech.theme.image_view_log_in_size

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: CourseViewModel = hiltViewModel()
) {
    val topAppBarScrollState = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier,
        topBar = {
            Toolbar(
                title = com.atech.theme.R.string.course,
                navigationIcon = Icons.Default.MenuOpen,
                onNavigationClick = {
                },
                scrollBehavior = topAppBarScrollState
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .consumeWindowInsets(it)
                .nestedScroll(topAppBarScrollState.nestedScrollConnection),
            contentPadding = it
        ) {
            items(1) {
                Box(
                    Modifier.fillMaxWidth()
                ) {
                    Image(
                        modifier = Modifier
                            .size(image_view_log_in_size)
                            .align(Alignment.Center),
                        painter = painterResource(id = R.drawable.ic_course),
                        contentDescription = null
                    )
                }
            }
            if (viewModel.courseDetails?.course != null)
                items(
                    items = viewModel.courseDetails.course,
                    key = { item -> item.name + item.sem }) { details ->
                    CourseItem(
                        details = details,
                    ) {
                        viewModel.onEvent(CourseEvents.NavigateToSemChoose(details))
                        navController.navigate(
                            CourseScreenRoute.SemChooseScreen.route
                        )
                    }
                }
            singleElement()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CourseScreenPreview() {
    BITAppTheme {
        CourseScreen()
    }
}
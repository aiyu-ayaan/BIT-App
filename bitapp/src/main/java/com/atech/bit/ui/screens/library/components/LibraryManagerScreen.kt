package com.atech.bit.ui.screens.library.components

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.atech.bit.R
import com.atech.bit.ui.comman.BackToolbar
import com.atech.bit.ui.comman.ImageIconButton
import com.atech.bit.ui.screens.library.LibraryManagerViewModel
import com.atech.bit.ui.theme.BITAppTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LibraryManagerScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController = rememberNavController(),
    viewModel: LibraryManagerViewModel = hiltViewModel()
) {
    val items = viewModel.libraryList.value
    Scaffold(
        modifier = modifier,
        topBar = {
            BackToolbar(
                title = R.string.library,
                onNavigationClick = {
                    navHostController
                        .navigateUp()
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    ImageIconButton(
                        icon = Icons.Outlined.DeleteSweep,
                        onClick = {

                        }
                    )
                }, floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                    },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(imageVector = Icons.Outlined.Edit, contentDescription = null)
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.consumeWindowInsets(it),
            contentPadding = it
        ) {
            items(items, key = { it1 -> it1.id }) {model->
                LibraryItem(
                    model = model
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LibraryManagerScreenPreview() {
    BITAppTheme {
        LibraryManagerScreen()
    }
}
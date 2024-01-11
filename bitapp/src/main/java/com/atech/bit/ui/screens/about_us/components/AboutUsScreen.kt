/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.about_us.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.atech.bit.R
import com.atech.bit.ui.comman.BackToolbar
import com.atech.bit.ui.comman.BottomPadding
import com.atech.bit.ui.comman.singleElement
import com.atech.bit.ui.navigation.AboutUsRoute
import com.atech.bit.ui.screens.about_us.AboutUsViewModel
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.captionColor
import com.atech.bit.ui.theme.dividerOrCardColor
import com.atech.bit.ui.theme.grid_1
import com.atech.bit.utils.getVersion
import com.atech.core.datasource.retrofit.model.toMap
import com.atech.core.utils.PRIVACY_POLICY
import com.atech.core.utils.openLinks
import com.atech.core.utils.openPlayStore


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AboutUsScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: AboutUsViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val aboutUsModel = viewModel.aboutUsModel.value.toMap()
    val context = LocalContext.current
    Scaffold(modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        BackToolbar(
            title = "About Us", onNavigationClick = {
                navController.popBackStack()
            }, scrollBehavior = scrollBehavior
        )
    }) {
        LazyColumn(
            modifier = Modifier.consumeWindowInsets(it), contentPadding = it
        ) {
            singleElement(key = "Header") { AboutUsHeader() }
            aboutUsModel.forEach { (type, list) ->
                stickyHeader {
                    Text(
                        modifier = Modifier.padding(grid_1),
                        text = type,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.captionColor
                    )
                }
                items(list) { item ->
                    DevItem(
                        devs = item,
                        onClick = {
                            viewModel.setCurrentClickDev(
                                item
                            )
                            navController
                                .navigate(
                                    AboutUsRoute.DevDetailsScreen.route
                                )
                        }
                    )
                }
            }
            val list = listOf(
                AboutUsButtonModel(
                    title = "Credits",
                    onClick = {
                        navController
                            .navigate(
                                AboutUsRoute.CreditScreen.route
                            )
                    }
                ),
                AboutUsButtonModel(title = "Privacy Policy", onClick = {
                    PRIVACY_POLICY.openLinks(context)
                }),
                AboutUsButtonModel(title = "View in Play Store", onClick = {
                    context.openPlayStore(context.packageName)
                }),
            )
            singleElement("buttons") {
                OutlinedCard(
                    modifier = Modifier.padding(grid_1),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.dividerOrCardColor),
                ) {
                    Column {
                        list.forEach { item ->
                            AboutUsButtons(
                                model = item
                            )
                        }
                    }
                }
            }
            singleElement("bottomPadding") { BottomPadding() }
        }
    }
}

@Composable
private fun AboutUsButtons(
    modifier: Modifier = Modifier, model: AboutUsButtonModel
) {
    Surface(modifier = modifier
        .fillMaxWidth()
        .clickable {
            model.onClick()
        }) {
        Column {
            TextButton(
                onClick = {
                    model.onClick()
                },
            ) {
                Text(
                    modifier = Modifier.padding(grid_1),
                    text = model.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Divider(
                color = MaterialTheme.colorScheme.dividerOrCardColor
            )
        }
    }
}

private data class AboutUsButtonModel(
    val title: String, val onClick: () -> Unit = {}
)

@Composable
private fun AboutUsHeader(
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier.padding(grid_1),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.dividerOrCardColor),
    ) {
        Row(
            modifier = Modifier
                .padding(grid_1)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = androidx.compose.material.MaterialTheme.typography.h4,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(grid_1))
                Text(
                    text = stringResource(R.string.developed_by_ayaan),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.s_version),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.captionColor
                )
                Text(
                    text = getVersion(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Image(
                painter = painterResource(id = R.drawable.ic_ayaan_beta),
                contentDescription = null,
                modifier = Modifier.size(116.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun AboutUseScreenPreview() {
    BITAppTheme {

    }
}
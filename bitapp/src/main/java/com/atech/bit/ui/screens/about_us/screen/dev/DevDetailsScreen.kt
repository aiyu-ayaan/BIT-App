/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.about_us.screen.dev

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.atech.bit.R
import com.atech.bit.ui.comman.BackToolbar
import com.atech.bit.ui.comman.ImageLoader
import com.atech.bit.ui.comman.singleElement
import com.atech.bit.ui.navigation.DeepLinkRoutes
import com.atech.bit.ui.navigation.navigateWithDeepLink
import com.atech.bit.ui.screens.about_us.AboutUsViewModel
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.captionColor
import com.atech.bit.ui.theme.dividerOrCardColor
import com.atech.bit.ui.theme.grid_1
import com.atech.bit.ui.theme.image_view_about_us_size
import com.atech.core.datasource.retrofit.model.Devs
import com.atech.core.utils.openLinks

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevDetailsScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: AboutUsViewModel = hiltViewModel()
) {
    val currentDev = viewModel.currentClickDev.value
    Scaffold(
        modifier = modifier,
        topBar = {
            BackToolbar(
                title = currentDev.name,
                onNavigationClick = {
                    navController.popBackStack()
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .consumeWindowInsets(it),
            contentPadding = it
        ) {
            singleElement("dev_details") {
                DevImageAndName(
                    devs = currentDev,
                    onImageClick = {
                        navController
                            .navigateWithDeepLink(
                                DeepLinkRoutes.ViewImageRoute(
                                    currentDev.imageLink
                                )
                            )
                    }
                )
            }
            val linksMap = getLinksMap(currentDev)
            singleElement("Links") {
                OutlinedCard(
                    modifier = Modifier.padding(grid_1),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.dividerOrCardColor),
                ) {
                    Column {
                        linksMap.forEach { triple ->
                            AboutUsButtons(pair = triple)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DevImageAndName(
    modifier: Modifier = Modifier,
    devs: Devs,
    onImageClick: () -> Unit = {}
) {
    OutlinedCard(
        modifier = modifier.padding(grid_1),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.dividerOrCardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(grid_1),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            ImageLoader(
                modifier = Modifier
                    .size(image_view_about_us_size)
                    .clickable { onImageClick.invoke() },
                imageUrl = devs.imageLink,
                isRounderCorner = true
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(grid_1)
            ) {
                Text(
                    text = devs.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = devs.des,
                    style = androidx.compose.material.MaterialTheme.typography.caption,
                    color = MaterialTheme.colorScheme.captionColor
                )
            }
        }
    }
}

@Composable
private fun AboutUsButtons(
    modifier: Modifier = Modifier,
    pair: Triple<String, String, Int>,
) {
    val context = LocalContext.current
    Surface(modifier = modifier
        .fillMaxWidth()
        .clickable {
            pair.second.openLinks(context)
        }) {
        Column {
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    pair.second.openLinks(context)
                },
            ) {
                Icon(
                    painter = painterResource(id = pair.third),
                    contentDescription = pair.first,
                )
                Text(
                    modifier = Modifier
                        .padding(grid_1)
                        .weight(1f),
                    text = pair.first,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
            Divider(
                color = MaterialTheme.colorScheme.dividerOrCardColor
            )
        }
    }
}


val getLinksMap: (Devs) -> List<Triple<String, String, Int>> = { dev: Devs ->
    listOf(
        Triple("Website", dev.website, R.drawable.ic_web_page),
        Triple("Stackoverflow", dev.stackoverflow, R.drawable.ic_stackoverflow),
        Triple("Github", dev.github, R.drawable.ic_github),
        Triple("Linkedin", dev.linkedin, R.drawable.ic_linkedin),
        Triple("Instagram", dev.instagram, R.drawable.ic_instagram)
    ).filter { it.second.isNotEmpty() }
}

@Preview(showBackground = true)
@Composable
private fun DevDetailsScreenPreview() {
    BITAppTheme {
        DevDetailsScreen()
    }
}
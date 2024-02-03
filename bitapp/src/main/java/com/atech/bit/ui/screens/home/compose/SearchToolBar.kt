/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.home.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuOpen
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atech.bit.R
import com.atech.bit.ui.comman.ImageIconButton
import com.atech.bit.ui.comman.ImageLoader
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.grid_1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchToolBar(
    modifier: Modifier = Modifier,
    query: String = "",
    onQueryChange: (String) -> Unit = {},
    onSearch: (String) -> Unit = {},
    active: Boolean = false,
    onActiveChange: (Boolean) -> Unit = {},
    onTrailingIconClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLeadingIconClick: () -> Unit = {},
    url: String? = null,
    contents: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surface
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SearchBar(
            modifier = Modifier
                .semantics { traversalIndex = -1f }
                .weight(1f)
                .padding(start = if (!active) grid_1 else 0.dp),
            query = query,
            onQueryChange = onQueryChange,
            onSearch = onSearch,
            active = active,
            onActiveChange = onActiveChange,
            placeholder = {
                Text(text = stringResource(id = R.string.search))
            },
            leadingIcon = {
                AnimatedVisibility(
                    visible = active,
                    enter = slideInHorizontally(
                        animationSpec = spring(),
                        initialOffsetX = { 500 },
                    ) + fadeIn(),
                    exit = scaleOut()
                            + fadeOut()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = stringResource(id = R.string.search),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                AnimatedVisibility(
                    visible = !active,
                    enter = slideInHorizontally() + fadeIn(),
                    exit = slideOutHorizontally() + fadeOut()
                ) {
                    ImageIconButton(
                        modifier = Modifier.weight(.2f),
                        icon = Icons.AutoMirrored.Outlined.MenuOpen,
                        tint = MaterialTheme.colorScheme.primary,
                        onClick = onLeadingIconClick,
                        contextDes = R.string.drawer
                    )
                }
            },
            trailingIcon = {
                AnimatedVisibility(visible = active) {
                    ImageIconButton(
                        icon = Icons.Outlined.Close,
                        contextDes = R.string.search,
                        onClick = onTrailingIconClick,
                        tint = MaterialTheme.colorScheme.primary,

                        )
                }
            }
        ) {
            contents.invoke()
        }
        AnimatedVisibility(visible = !active) {
            ImageIconButton(
                modifier = Modifier.weight(.2f),
                icon = Icons.Outlined.Notifications,
                tint = MaterialTheme.colorScheme.primary,
                onClick = onNotificationClick,
                contextDes = R.string.notice
            )
        }
        AnimatedVisibility(visible = !active) {
            ProfileImage(
                modifier = Modifier.weight(.2f),
                url = url,
                onClick = onProfileClick,
            )
        }
    }
}

@Composable
fun ProfileImage(
    modifier: Modifier = Modifier,
    url: String? = null,
    onClick: () -> Unit = {},
) {
    AnimatedVisibility(visible = url != null) {
        IconButton(onClick = onClick) {
            ImageLoader(
                modifier = Modifier
                    .size(30.dp),
                imageUrl = url,
                isRounderCorner = true
            )
        }
    }
    AnimatedVisibility(visible = url == null) {
        ImageIconButton(
            modifier = modifier,
            icon = Icons.Default.AccountCircle,
            tint = MaterialTheme.colorScheme.primary,
            onClick = onClick,
            contextDes = R.string.profile,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchToolBarPreview() {
    BITAppTheme {
        SearchToolBar()
    }
}
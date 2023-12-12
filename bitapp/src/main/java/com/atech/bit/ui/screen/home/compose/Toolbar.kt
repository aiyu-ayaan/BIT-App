package com.atech.bit.ui.screen.home.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.MenuOpen
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atech.components.ImageIconButton
import com.atech.theme.BITAppTheme
import com.atech.theme.grid_1
import com.atech.theme.grid_2

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
    contents: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SearchBar(
            modifier = Modifier
                .weight(1f)
                .padding(start = if (!active) grid_1 else 0.dp),
            query = query,
            onQueryChange = onQueryChange,
            onSearch = onSearch,
            active = active,
            onActiveChange = onActiveChange,
            placeholder = {
                Text(text = stringResource(id = com.atech.theme.R.string.search))
            },
            leadingIcon = {
                AnimatedVisibility(
                    visible = active,
                    enter = slideInVertically() + fadeIn(),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = stringResource(id = com.atech.theme.R.string.search),
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
                        icon = Icons.Outlined.MenuOpen,
                        tint = MaterialTheme.colorScheme.primary,
                        onClick = onLeadingIconClick,
                        contextDes = com.atech.theme.R.string.drawer
                    )
                }
            },
            trailingIcon = {
                AnimatedVisibility(visible = active) {
                    ImageIconButton(
                        icon = Icons.Outlined.Close,
                        contextDes = com.atech.theme.R.string.search,
                        onClick = onTrailingIconClick,
                        tint = MaterialTheme.colorScheme.primary
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
                contextDes = com.atech.theme.R.string.notice
            )
        }
        AnimatedVisibility(visible = !active) {
            ImageIconButton(
                modifier = Modifier.weight(.2f),
                icon = Icons.Default.AccountCircle,
                tint = MaterialTheme.colorScheme.primary,
                onClick = onProfileClick,
                contextDes = com.atech.theme.R.string.profile
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchToolBarPreview() {
    BITAppTheme {
        SearchToolBar()
    }
}
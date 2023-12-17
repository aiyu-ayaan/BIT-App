package com.atech.bit.ui.comman

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.atech.bit.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackToolbar(
    modifier: Modifier = Modifier,
    @StringRes title: Int = R.string.back,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavigationClick: () -> Unit = { }
) {
    Toolbar(
        modifier = modifier,
        title = title,
        navigationIcon = Icons.Default.ArrowBack,
        scrollBehavior = scrollBehavior,
        onNavigationClick = onNavigationClick,
        actions = actions
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackToolbar(
    modifier: Modifier = Modifier,
    title: String = "",
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavigationClick: () -> Unit = { }
) {
    Toolbar(
        modifier = modifier,
        title = title,
        navigationIcon = Icons.Default.ArrowBack,
        scrollBehavior = scrollBehavior,
        onNavigationClick = onNavigationClick,
        actions = actions
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Toolbar(
    modifier: Modifier = Modifier,
    @StringRes title: Int = R.string.app_name,
    navigationIcon: ImageVector? = null,
    actions: @Composable RowScope.() -> Unit = {},
    onNavigationClick: () -> Unit = { },
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(modifier =
    modifier.fillMaxWidth(), title = {
        Text(text = stringResource(id = title))
    }, navigationIcon = {
        if (navigationIcon == null) return@TopAppBar
        ImageIconButton(
            onClick = onNavigationClick,
            icon = navigationIcon,
            contextDes = R.string.back,
            tint = MaterialTheme.colorScheme.primary
        )
    }, actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.primary
        ),
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Toolbar(
    modifier: Modifier = Modifier,
    title: String = "",
    navigationIcon: ImageVector? = null,
    actions: @Composable RowScope.() -> Unit = {},
    onNavigationClick: () -> Unit = { },
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(modifier =
    modifier.fillMaxWidth(), title = {
        Text(text = title)
    }, navigationIcon = {
        if (navigationIcon == null) return@TopAppBar
        ImageIconButton(
            onClick = onNavigationClick,
            icon = navigationIcon,
            contextDes = R.string.back,
            tint = MaterialTheme.colorScheme.primary
        )
    }, actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.primary
        ),
        scrollBehavior = scrollBehavior
    )
}



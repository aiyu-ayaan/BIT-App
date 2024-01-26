/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.settings.component

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.AppSettingsAlt
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.NotificationImportant
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.atech.bit.R
import com.atech.bit.ui.activity.main.MainViewModel
import com.atech.bit.ui.activity.main.ThemeEvent
import com.atech.bit.ui.activity.main.ThemeMode
import com.atech.bit.ui.activity.main.ThemeState
import com.atech.bit.ui.comman.BackToolbar
import com.atech.bit.ui.comman.BottomPadding
import com.atech.bit.ui.comman.PreferenceItem
import com.atech.bit.ui.comman.PreferenceItemBorder
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.captionColor
import com.atech.bit.ui.theme.dividerOrCardColor
import com.atech.bit.ui.theme.grid_0_5
import com.atech.bit.ui.theme.grid_1
import com.atech.bit.ui.theme.grid_2


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: MainViewModel = hiltViewModel()
) {
    val state by viewModel.themeState
    val scrollState = rememberScrollState()
    val topBarState = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(modifier = modifier.nestedScroll(topBarState.nestedScrollConnection), topBar = {
        BackToolbar(
            title = "Settings", onNavigationClick = {
                navController.navigateUp()
            }, scrollBehavior = topBarState
        )
    }) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(scrollState)
        ) {
            SettingTitle(text = stringResource(id = R.string.display))
            DisplayCard()
            Spacer(modifier = Modifier.height(grid_2))
            PreferenceItem(title = stringResource(R.string.dynamic_color),
                description = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) stringResource(R.string.apply_colors_from_wallpaper_to_the_app_theme)
                else stringResource(R.string.only_available_on_android_12_or_above),
                icon = Icons.Outlined.Palette,
                isChecked = state.isDynamicColorActive && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
                onClick = {
                    viewModel.onThemeChange(
                        ThemeEvent.ChangeTheme(
                            state.copy(
                                isDynamicColorActive = !state.isDynamicColorActive
                            )
                        )
                    )
                })
            var isThemeChangeVisible by rememberSaveable {
                mutableStateOf(false)
            }
            val isDarkTheme = isSystemInDarkTheme()
            PreferenceItemBorder(title = stringResource(R.string.dark_mode),
                description = if (state.isDarkTheme.getDarkEnable()) stringResource(R.string.on)
                else stringResource(
                    R.string.off
                ),
                isChecked = state.isDarkTheme.getDarkEnable(),
                icon = if (state.isDarkTheme.getDarkEnable()) Icons.Outlined.DarkMode
                else Icons.Outlined.LightMode,
                onChecked = {
                    viewModel.onThemeChange(
                        saveTheme(
                            state,
                            isDarkTheme
                        )
                    )
                },
                onClick = {
                    isThemeChangeVisible = !isThemeChangeVisible
                })
            AnimatedVisibility(visible = isThemeChangeVisible) {
                Divider(
                    color = MaterialTheme.colorScheme.dividerOrCardColor
                )
                Column {
                    ThemeMode.entries.toList().forEach { mode ->
                        ThemeRadioButton(
                            themeType = mode,
                            selected = state.isDarkTheme == mode,
                            onClick = {
                                viewModel.onThemeChange(
                                    ThemeEvent.ChangeTheme(
                                        state.copy(
                                            isDarkTheme = mode
                                        )
                                    )
                                )
                            })
                    }
                }
            }
            Spacer(modifier = Modifier.height(grid_2))
            val (notice, event, app) = viewModel.appNotification.value
            SettingTitle(text = stringResource(R.string.app_notification))
            PreferenceItem(
                title = stringResource(id = R.string.notice),
                description = stringResource(R.string.all_college_related_notification),
                icon = Icons.Outlined.NotificationImportant,
                isChecked = notice,
                onClick = {
                    viewModel
                        .setAppNotification(
                            viewModel.appNotification.value.copy(
                                notice = !notice
                            )
                        )
                }
            )
            Spacer(modifier = Modifier.height(grid_1))
            PreferenceItem(
                title = stringResource(R.string.event),
                description = stringResource(R.string.all_society_related_notification),
                icon = Icons.Outlined.EmojiEvents,
                isChecked = event,
                onClick = {
                    viewModel
                        .setAppNotification(
                            viewModel.appNotification.value.copy(
                                event = !event
                            )
                        )
                }
            )
            Spacer(modifier = Modifier.height(grid_1))
            PreferenceItem(
                title = stringResource(R.string.app),
                description = stringResource(R.string.all_app_related_notification),
                icon = Icons.Outlined.AppSettingsAlt,
                isChecked = app,
                onClick = {
                    viewModel
                        .setAppNotification(
                            viewModel.appNotification.value.copy(
                                app = !app
                            )
                        )
                }
            )
            Spacer(modifier = Modifier.height(grid_2))
            SettingTitle(text = stringResource(R.string.experimental))
            PreferenceItem(
                title = stringResource(id = com.atech.chat.R.string.tutortalk),
                description = "All new Chat feature powered by Gemini Pro",
                icon = Icons.AutoMirrored.Outlined.Chat,
                isChecked = viewModel.isChatScreenEnable.value,
                onClick = {
                    viewModel.onEvent(
                        MainViewModel.SharedEvents.IsChatScreenEnable
                    )
                }
            )
            BottomPadding()
        }
    }
}

@Composable
fun ThemeRadioButton(
    modifier: Modifier = Modifier,
    selected: Boolean = true,
    onClick: () -> Unit = { },
    themeType: ThemeMode,
) {
    Surface(modifier = modifier
        .fillMaxWidth()
        .clickable {
            onClick()
        }) {
        Row(
            modifier = Modifier
                .padding(grid_2)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = themeType.name.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(end = grid_2, start = grid_2)
            )
            RadioButton(
                selected = selected, onClick = null
            )
        }
    }
}


private fun saveTheme(state: ThemeState, isDarkTheme: Boolean) = ThemeEvent.ChangeTheme(
    state.copy(
        isDarkTheme = when (state.isDarkTheme) {
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.LIGHT
            ThemeMode.SYSTEM -> if (isDarkTheme) ThemeMode.LIGHT
            else ThemeMode.DARK
        }
    )
)

@Composable
private fun ThemeMode.getDarkEnable() = when (this) {
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
}


val thumbnails = listOf(
    R.drawable.sample1,
    R.drawable.sample2,
    R.drawable.sample3,
)

@Composable
fun DisplayCard() {
    val thumb by rememberSaveable {
        mutableStateOf(thumbnails.random())
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = grid_2),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.dividerOrCardColor,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = thumb),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding()
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f, matchHeightConstraintsFirst = true)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface
                    )
            ) {
                Column {
                    Text(
                        text = "Sample Title",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.captionColor,
                        modifier = Modifier.padding(horizontal = grid_1, vertical = grid_0_5)
                    )
                    Text(
                        text = "Sample Description",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.captionColor,
                        modifier = Modifier.padding(horizontal = grid_1, vertical = grid_0_5)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(
                        MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.medium
                    )
            )
        }

    }
}


@Composable
fun SettingTitle(
    text: String
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.captionColor,
        modifier = Modifier
            .padding(bottom = grid_1)
            .padding(horizontal = grid_2)
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingScreenPreview() {
    BITAppTheme {
        SettingScreen()
    }
}
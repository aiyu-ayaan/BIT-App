/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.force

import android.app.Activity
import androidx.annotation.Keep
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atech.bit.R
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.grid_1
import com.atech.core.utils.openLinks

@Composable
fun ForceScreen(
    modifier: Modifier = Modifier,
    model: ForceScreenModel = ForceScreenModel(
        title = "About App",
        description = "This app is not available in your country",
        link = "https://play.google.com/store/apps/details?id=com.atech.bit",
        maxVersion = -1,
        isEnable = true
    )
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.warning_img),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        Text(
            text = model.title,
            modifier = Modifier,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = model.description,
            modifier = Modifier,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(grid_1))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(grid_1),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = {
                (context as Activity).finishAfterTransition()
            }) {
                Icon(
                    imageVector = Icons.Outlined.ThumbUp,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(grid_1))
                Text(text = "Ok")
            }
            Spacer(modifier = modifier.width(grid_1))
            if (model.link != null) {
                TextButton(onClick = {
                    model.link.openLinks(context)
                }) {
                    Icon(
                        imageVector = Icons.Outlined.OpenInNew,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(grid_1))
                    Text(text = "Open")
                }
            }
        }
    }
}

@Keep
data class ForceScreenModel(
    val title: String ="",
    val description: String ="",
    val link: String? = null,
    val isEnable: Boolean = false,
    val maxVersion: Int = -1
)

@Preview(showBackground = true)
@Composable
private fun ForceScreenPreview() {
    BITAppTheme {
    }
}
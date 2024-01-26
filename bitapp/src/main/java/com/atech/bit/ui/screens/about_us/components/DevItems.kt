/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.about_us.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atech.bit.ui.comman.ImageLoader
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.captionColor
import com.atech.bit.ui.theme.dividerOrCardColor
import com.atech.bit.ui.theme.grid_1
import com.atech.core.datasource.retrofit.model.Devs

@Composable
fun DevItem(
    modifier: Modifier = Modifier, devs: Devs = Devs(
        1,
        "Ayaan",
        "https://avatars.githubusercontent.com/u/56148278?v=4",
        "",
        "",
        "",
        "",
        "",
        "Dev"
    ), onClick: () -> Unit = {}
) {
    Surface(modifier = modifier
        .padding(bottom = grid_1)
        .clickable {
            onClick()
        }) {
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = grid_1),
            border = BorderStroke(
                1.dp, MaterialTheme.colorScheme.dividerOrCardColor
            ),
        ) {
            Row(
                modifier = modifier.padding(grid_1),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                ImageLoader(
                    imageUrl = devs.imageLink,
                    modifier = Modifier.size(30.dp),
                    isRounderCorner = true
                )
                Spacer(modifier = Modifier.padding(grid_1))
                Column {
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
}

@Preview(showBackground = true)
@Composable
private fun DevItemPreview() {
    BITAppTheme {
        DevItem()
    }
}
/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.society.components.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atech.bit.ui.comman.ImageLoader
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.borderColor
import com.atech.bit.ui.theme.captionColor
import com.atech.bit.ui.theme.grid_0_5
import com.atech.bit.ui.theme.grid_1
import com.atech.core.datasource.retrofit.model.Society


@Composable
fun SocietyType(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(horizontal = grid_1),
        color = MaterialTheme.colorScheme.captionColor
    )
}


@Composable
fun SocietyItem(
    modifier: Modifier = Modifier,
    model: Society,
    onClick: (Society) -> Unit = {}
) {
    Surface(
        modifier = modifier
            .clickable {
                onClick(model)
            },
    ) {
        OutlinedCard(
            modifier = modifier
                .padding(vertical = grid_0_5, horizontal = grid_1)
                .fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.borderColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(grid_1),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ImageLoader(
                    modifier = Modifier.size(80.dp),
                    imageUrl = model.logo,
                    isRounderCorner = true
                )
                Spacer(modifier = Modifier.width(grid_1))
                Text(
                    text = model.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(grid_1),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SocietyItemPreview() {
    BITAppTheme {
        SocietyType(title = "Society")
    }
}
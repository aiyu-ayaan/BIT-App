/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.home.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atech.bit.ui.comman.singleElement
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.captionColor
import com.atech.bit.ui.theme.dividerOrCardColor
import com.atech.bit.ui.theme.grid_0_5
import com.atech.bit.ui.theme.grid_1
import com.atech.bit.ui.theme.grid_2
import com.atech.core.datasource.retrofit.model.Holiday


@Composable
fun HomeTitle(
    title: String = "Theory", endItem: String? = null, endItemClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = grid_1, horizontal = grid_1),

        ) {
        Text(
            modifier = Modifier.align(Alignment.TopStart),
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.captionColor
        )
        if (endItem != null) Text(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clickable {
                    endItemClick?.invoke()
                },
            text = "See All",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}


fun LazyListScope.showHoliday(
    items: List<Holiday> = emptyList(),
    endItem: String? = null,
    endItemClick: (() -> Unit)? = null
) = this.apply {
    if (items.isEmpty()) {
        return@apply
    }
    singleElement(key = "holiday_title") {
        HomeTitle(
            title = "Holidays",
            endItem = endItem,
            endItemClick = endItemClick
        )
    }
    items(items, key = { it.occasion + it.date }) {
        HolidayMainComposeItem(model = it)
    }
}

@Composable
fun HolidayMainComposeItem(
    modifier: Modifier = Modifier,
    model: Holiday
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = grid_0_5, horizontal = grid_2),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.dividerOrCardColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.background(
                    MaterialTheme.colorScheme.primary,
                )
            ) {
                Text(
                    text = model.date,
                    modifier = Modifier.padding(grid_2),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Column(
                modifier = modifier
                    .padding(start = grid_1)
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = model.occasion,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = model.day,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HolidayMainComposeItemPreview() {
    BITAppTheme {
        HomeTitle()
    }
}
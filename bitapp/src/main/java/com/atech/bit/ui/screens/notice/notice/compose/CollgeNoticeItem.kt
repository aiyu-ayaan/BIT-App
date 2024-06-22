/*
 *  Created by Aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.notice.notice.compose


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.dividerOrCardColor
import com.atech.bit.ui.theme.grid_0_5
import com.atech.bit.ui.theme.grid_1
import com.atech.core.datasource.retrofit.model.CollegeNotice

@Composable
internal fun CollegeNoticeItem(
    modifier: Modifier = Modifier,
    model: CollegeNotice,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .clickable { onClick.invoke() }
            .fillMaxWidth()
    ) {
        OutlinedCard(
            modifier = Modifier.padding(top = grid_0_5),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.dividerOrCardColor)
        ) {
            Column(
                modifier = Modifier
                    .padding(grid_1)
                    .fillMaxWidth()
            ) {
                Text(
                    text = model.title.lowercase()
                        .replaceFirstChar { it.uppercaseChar() },
                    modifier = Modifier
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.titleSmall,
                )
                Spacer(Modifier.height(grid_1))
                Text(
                    text = model.date,
                    style = androidx.compose.material.MaterialTheme.typography.caption,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CollegeNoticeItemPreview() {
    BITAppTheme {
        CollegeNoticeItem(
            model = CollegeNotice(
                title = "This is for Demo",
                date = "03 05 2000",
                link = ""
            )
        )
    }
}
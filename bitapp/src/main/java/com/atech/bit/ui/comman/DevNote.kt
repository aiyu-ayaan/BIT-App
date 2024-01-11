/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.comman

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.atech.bit.R
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.course_image_size
import com.atech.bit.ui.theme.grid_1


const val GITHUB_LINK = "https://github.com/aiyu-ayaan"

@Composable
fun DevNote(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.clickable {
            onClick.invoke()
        }
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(grid_1)
                .height(course_image_size),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .size(course_image_size),
                painter = painterResource(id = R.drawable.ic_dev_image),
                contentDescription = "dev"
            )
            Spacer(modifier = Modifier.width(grid_1))
            Text(
                text = "The owner of this application is a student of BIT Lalpur and does not have any affiliation with BIT itself.\n" +
                        "This app is now open source\uD83E\uDD17.\nKnow About Dev.",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DevNotePreview() {
    BITAppTheme {
        DevNote()
    }
}
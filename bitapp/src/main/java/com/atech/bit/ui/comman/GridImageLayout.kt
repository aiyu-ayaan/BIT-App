/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.comman

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.atech.bit.R
import com.atech.bit.ui.theme.captionColor
import com.atech.bit.ui.theme.grid_1
import com.atech.bit.ui.theme.grid_2
import com.atech.core.datasource.firebase.firestore.Attach
import kotlin.math.ceil


@Composable
fun ColumnScope.GridImageLayout(
    list: List<Attach>,
    onClick: (String) -> Unit
) = this.apply {
    Column {
        Spacer(modifier = Modifier.height(grid_2))
        Text(
            text = stringResource(R.string.attached_images),
            color = MaterialTheme.colorScheme.captionColor,
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(grid_1))
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(3),
            verticalItemSpacing = 4.dp,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            content = {
                items(list.size) { attach ->
                    ImageLoader(
                        imageUrl = list[attach].link ?: "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clickable { onClick(list[attach].link!!) },
                        contentScale = ContentScale.Crop,
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    calculateSize(list.size)
                )
        )
    }
}

fun calculateSize(size: Int) = (ceil(size.toFloat() / 3.0) * 200).dp
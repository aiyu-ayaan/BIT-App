/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.home.compose

import androidx.annotation.Keep
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atech.bit.ui.comman.LottieAnimLink
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.drawerColor
import com.atech.bit.ui.theme.grid_1
import com.atech.bit.ui.theme.grid_2
import com.atech.core.utils.openLinks


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppAlertDialog(
    modifier: Modifier = Modifier,
    model: EventAlertModel,
    onDismissRequest: () -> Unit = {}
) {
    AlertDialog(onDismissRequest = onDismissRequest) {
        EventAlertDialog(
            modifier = modifier,
            model = model,
            onDismissRequest = onDismissRequest
        )
    }
}

@Composable
fun EventAlertDialog(
    modifier: Modifier = Modifier,
    model: EventAlertModel,
    onDismissRequest: () -> Unit = {},

    ) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.drawerColor,
        ),
        elevation = CardDefaults.elevatedCardElevation(),
        shape = RoundedCornerShape(grid_2)
    ) {
        Column(
            modifier = Modifier
                .padding(grid_1)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieAnimLink(
                link = model.lottieLink,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Spacer(modifier = Modifier.height(grid_2))
            Text(

                text = model.title,
                modifier = Modifier,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(grid_1))
            Text(
                text = model.body,
                style = MaterialTheme.typography.bodyMedium,
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
                TextButton(onClick = onDismissRequest) {
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
                        model.link.openLinks(
                            context = context
                        )
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.width(grid_1))
                        Text(text = "Open")
                    }
                }
            }
        }
    }
}

@Keep
data class EventAlertModel(
    val title: String,
    val body: String,
    val lottieLink: String,
    val link: String? = null,
    val isShow: Boolean = true,
    val maxTimesToShow: Int = 5,
    val version: Int = 0
)


val eventAlertModel = EventAlertModel(
    "BIT APP",
    """
        Welcome to BIT App.
    """.trimIndent(),
    lottieLink = "https://lottie.host/aef42e66-22ea-4aed-a84f-d3c8029a2488/xApVTSr32n.json",
)

@Preview(showBackground = true)
@Composable
private fun EventAlertDialogPreview() {
    BITAppTheme {
        EventAlertDialog(
            model = eventAlertModel
        )
    }
}
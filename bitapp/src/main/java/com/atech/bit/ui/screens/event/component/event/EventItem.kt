package com.atech.bit.ui.screens.event.component.event

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atech.bit.ui.comman.ImageLoader
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.captionColor
import com.atech.bit.ui.theme.dividerOrCardColor
import com.atech.bit.ui.theme.grid_1
import com.atech.bit.ui.theme.grid_2
import com.atech.core.datasource.firebase.firestore.Attach
import com.atech.core.datasource.firebase.firestore.EventModel
import com.atech.core.utils.getDate

@Composable
fun EventItem(
    modifier: Modifier = Modifier,
    model: EventModel,
    onEventClick : (EventModel) -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth()
            .clickable { onEventClick.invoke(model) }
    ) {
        OutlinedCard(
            modifier = Modifier.padding(grid_1),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.dividerOrCardColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(grid_1),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(end = grid_1),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        Modifier.padding(horizontal = grid_1),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = model.society ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(grid_1))
                        Icon(
                            modifier = Modifier.size(
                                MaterialTheme.typography.bodyMedium.fontSize.value.dp
                            ),
                            imageVector = Icons.Outlined.DateRange,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(grid_1))
                        Text(
                            text = model.created?.getDate() ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    ImageLoader(
                        modifier = Modifier.size(
                            30.dp
                        ),
                        imageUrl = model.logo_link ?: "",
                        isRounderCorner = true
                    )
                }
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = grid_2),
                text = model.title ?: "",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(grid_1))
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = grid_2),
                text = model.content ?: "",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.captionColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(grid_1))
            if (model.attach != null && model.attach?.isNotEmpty() == true)
                AttachCompose(
                    list = model.attach!!
                )
        }
    }
}


@Composable
fun AttachCompose(
    modifier: Modifier = Modifier, list: List<Attach>
) {
    LazyRow(modifier = modifier.fillMaxWidth()) {
        items(list.size) {
            AttachItems(
                modifier = Modifier.padding(start = grid_1),
                model = list[it]
            )
        }
    }
}

@Composable
fun AttachItems(
    modifier: Modifier = Modifier, model: Attach
) {
    ImageLoader(
        modifier = modifier
            .padding(grid_1)
            .width(120.dp)
            .height(96.dp),
        imageUrl = model.link,
    )
}


@Preview(showBackground = true)
@Composable
private fun EventItemPreview() {
    BITAppTheme {
        EventItem(
            model = EventModel(
                title = "Title",
                content = "Content",
                insta_link = "insta_link",
                logo_link = "logo_link",
                path = "path",
                society = "Society",
                video_link = "video_link",
                created = System.currentTimeMillis(),
                attach = emptyList()
            )
        )
    }
}
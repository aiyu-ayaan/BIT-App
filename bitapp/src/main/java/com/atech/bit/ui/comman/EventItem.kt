package com.atech.bit.ui.comman

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.captionColor
import com.atech.bit.ui.theme.dividerOrCardColor
import com.atech.bit.ui.theme.grid_1
import com.atech.bit.ui.theme.grid_2
import com.atech.core.datasource.firebase.firestore.Attach
import com.atech.core.datasource.firebase.firestore.Db
import com.atech.core.datasource.firebase.firestore.EventModel
import com.atech.core.datasource.firebase.firestore.GetAttach
import com.atech.core.datasource.firebase.firestore.NoticeModel
import com.atech.core.utils.getDate
import kotlinx.coroutines.launch

@Composable
fun EventItem(
    modifier: Modifier = Modifier,
    model: EventModel,
    getAttach: GetAttach,
    onEventClick: (EventModel) -> Unit = {}
) {
    var attach by rememberSaveable {
        mutableStateOf(emptyList<Attach>())
    }
    val scope = rememberCoroutineScope()
    Surface(
        modifier = modifier
            .fillMaxWidth()
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
                    modifier = Modifier
                        .fillMaxWidth()
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
            scope.launch {
                getAttach.invoke(
                    Db.Event,
                    model.path!!,
                    action = {
                        attach = it
                    }
                )
            }
            AnimatedVisibility(visible = attach.isNotEmpty()) {
                AttachCompose(
                    list = attach
                )
            }
        }
    }
}

@Composable
fun NoticeItem(
    modifier: Modifier = Modifier,
    model: NoticeModel,
    onEventClick: (NoticeModel) -> Unit = {},
    getAttach: GetAttach
) {
    var attach by rememberSaveable {
        mutableStateOf(emptyList<Attach>())
    }
    val scope = rememberCoroutineScope()
    Surface(
        modifier = modifier
            .fillMaxWidth()
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = grid_1),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        Modifier.padding(horizontal = grid_1),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = model.sender ?: "",
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
                        imageUrl = model.getImageLinkNotification(),
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
                text = model.body ?: "",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.captionColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(grid_1))
            scope.launch {
                getAttach.invoke(
                    Db.Notice,
                    model.path!!,
                    action = {
                        attach = it
                    }
                )
            }
            AnimatedVisibility(visible = attach.isNotEmpty()) {
                AttachCompose(
                    list = attach
                )
            }
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


fun NoticeModel.getImageLinkNotification(): String = when (this.sender) {
    "App Notice" -> "https://firebasestorage.googleapis.com/v0/b/theaiyubit.appspot.com/o/Utils%2Fapp_notification.png?alt=media&token=0a7babfe-bf59-4d19-8fc0-98d7fde151a6"
    else -> "https://firebasestorage.googleapis.com/v0/b/theaiyubit.appspot.com/o/Utils%2Fcollege_notifications.png?alt=media&token=c5bbfda0-c73d-4af1-9c3c-cb29a99d126b"
}


@Preview(showBackground = true)
@Composable
private fun EventItemPreview() {
    BITAppTheme {

    }
}
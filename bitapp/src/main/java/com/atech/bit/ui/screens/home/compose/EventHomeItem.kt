package com.atech.bit.ui.screens.home.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atech.bit.ui.comman.ImageLoaderRounderCorner
import com.atech.bit.ui.comman.singleElement
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.grid_1
import com.atech.bit.ui.theme.grid_2
import com.atech.core.datasource.firebase.firestore.EventModel
import com.atech.core.usecase.Db
import com.atech.core.usecase.GetAttach


fun LazyListScope.showEvents(
    items: List<EventModel>,
    getAttach: GetAttach,
    endItem: String? = null,
    endItemClick: (() -> Unit)? = null
) = this.apply {
    if (items.isEmpty()) {
        return@apply
    }
    singleElement(key = "event_title") {
        HomeTitle(
            title = "Holidays", endItem = endItem, endItemClick = endItemClick
        )
    }
    singleElement(
        key = "LazyRow"
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            items(items.size) { index ->
                HomeEventItems(
                    model = items[index], getAttach = getAttach
                )
            }
        }
    }
}

@Composable
fun HomeEventItems(
    modifier: Modifier = Modifier,
    model: EventModel,
    getAttach: GetAttach,
    click: (() -> Unit) = {}
) {
    var rememberImageLink by rememberSaveable {
        mutableStateOf(model.logo_link)
    }
    LaunchedEffect(model.path) {
        getAttach.invoke(Db.Event, model.path!!, action = {
            if (it.isNotEmpty()) {
                rememberImageLink = it[0].link
            }
        })
    }
    Box(
        modifier = modifier
            .width(300.dp)
            .height(220.dp)
            .padding(vertical = grid_1, horizontal = grid_1)
            .clickable {
                click.invoke()
            },
    ) {

        ImageLoaderRounderCorner(
            modifier = modifier.fillMaxSize(),
            imageUrl = rememberImageLink,
            contentScale = ContentScale.Crop,
            isRounderCorner = grid_2
        )
        Text(
            text = model.title ?: "",
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(grid_1),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeEventItemsPreview() {
    BITAppTheme {

    }
}
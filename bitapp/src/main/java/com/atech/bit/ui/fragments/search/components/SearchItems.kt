package com.atech.bit.ui.fragments.search.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.atech.bit.R
import com.atech.bit.utils.DPS.GRID_1
import com.atech.bit.utils.DPS.GRID_2
import com.atech.bit.utils.getComposeColor
import com.atech.core.data.room.syllabus.SyllabusModel


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchedContent(
    modifier: Modifier = Modifier,
    state: List<SyllabusModel>,
    onClick: (SyllabusModel) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(bottom = GRID_2),
    ) {
        stickyHeader {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = LocalContext.current
                            .getComposeColor(me.relex.circleindicator.R.attr.colorSurface)
                    )
                    .padding(horizontal = GRID_2, vertical = GRID_1),
                text = stringResource(id = R.string.syllabus),
                style = MaterialTheme.typography.titleSmall,
            )
        }
        state.forEachIndexed { index, _ ->
            item {
                SyllabusComponent(
                    modifier = Modifier.padding(bottom = GRID_1),
                    state = state[index],
                    onClick = onClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SyllabusComponent(
    modifier: Modifier,
    state: SyllabusModel,
    onClick : (SyllabusModel) -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = LocalContext.current.getComposeColor(R.attr.bottomBar)
        ),
        onClick = {
            onClick(state)
        }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = state.subject,
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.code,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.width(GRID_1))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier
                            .width(GRID_2)
                            .wrapContentHeight(),
                        imageVector = Icons.Default.Star,
                        contentDescription = "Books",
                        colorFilter = ColorFilter.tint(
                            LocalContext.current.getComposeColor(androidx.appcompat.R.attr.colorPrimary)
                        )
                    )
                    Spacer(modifier = Modifier.width(GRID_1))
                    Text(
                        text = state.credits.toString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.width(GRID_1))
                    Image(
                        modifier = Modifier
                            .width(GRID_2)
                            .wrapContentHeight(),
                        painter = painterResource(id = R.drawable.ic_book),
                        contentDescription = "Books",
                        colorFilter = ColorFilter.tint(
                            LocalContext.current.getComposeColor(androidx.appcompat.R.attr.colorPrimary)
                        )
                    )
                    Spacer(modifier = Modifier.width(GRID_1))
                    Text(
                        text = state.type,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun LandingIcon(
    modifier: Modifier = Modifier,
    state: Boolean
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .width(128.dp)
                .wrapContentHeight(),
            painter = painterResource(id = R.drawable.ic_searching),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (state) stringResource(id = R.string.search_for_syllabus) else stringResource(
                id = R.string.no_subject_found
            ),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}
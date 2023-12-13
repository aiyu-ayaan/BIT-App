package com.atech.attendance.screen.attendance.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.MenuOpen
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.atech.attendance.AttendanceScreenRoutes
import com.atech.attendance.R
import com.atech.attendance.screen.attendance.AttendanceEvent
import com.atech.attendance.screen.attendance.AttendanceViewModel
import com.atech.components.ImageIconButton
import com.atech.components.ImageIconModel
import com.atech.core.data_source.room.attendance.AttendanceModel
import com.atech.theme.BITAppTheme
import com.atech.theme.dividerOrCardColor
import com.atech.theme.grid_0_5
import com.atech.theme.grid_1
import com.atech.theme.grid_2
import com.atech.utils.isScrollingUp

@OptIn(
    ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)
@Composable
fun AttendanceScreen(
    modifier: Modifier = Modifier,
    viewModel: AttendanceViewModel = hiltViewModel(),
    navController: NavController = rememberNavController()
) {
    val attendanceList = viewModel.attendance.collectAsLazyPagingItems()
    val lazyListState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var currentClickAttendance by rememberSaveable {
        mutableStateOf<AttendanceModel?>(null)
    }
    Scaffold(modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection), bottomBar = {
        AttendanceBottomAppbar(action = {
            navController.navigate(
                    AttendanceScreenRoutes.AddEditAttendanceScreen.route
                )
        })
    }, topBar = {
        TopAppBar(
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        text = "Goal : 80%",
                        modifier = Modifier.padding(start = grid_1),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(grid_1))
                    Text(
                        text = "😎",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(grid_1))
                    Text(
                        text = "Current : 75%",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(end = grid_1),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }, scrollBehavior = scrollBehavior
        )
    }) {
        val sheetState = rememberModalBottomSheetState()
        var isSheetOpen by rememberSaveable {
            mutableStateOf(false)
        }

        if (isSheetOpen && currentClickAttendance != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    isSheetOpen = false
                }, sheetState = sheetState
            ) {
                AttendanceCalenderView(
                    model = currentClickAttendance!!
                )
            }
        }

        LazyColumn(
            modifier = Modifier.consumeWindowInsets(it), contentPadding = it, state = lazyListState
        ) {
            items(count = attendanceList.itemCount,
                key = attendanceList.itemKey { model -> model.id },
                contentType = attendanceList.itemContentType { it1 -> it1.subject }) { index ->
                attendanceList[index]?.let { model ->
                    AttendanceItem(model = model, modifier = Modifier.animateItemPlacement(
                        animationSpec = spring(
                            dampingRatio = 2f, stiffness = 600f
                        )
                    ), onTickOrCrossClickClick = { clickItems, isPresent ->
                        viewModel.onEvent(
                                AttendanceEvent.ChangeAttendanceValue(
                                    attendanceModel = clickItems, isPresent = isPresent
                                )
                            )
                    }, onClick = {
                        currentClickAttendance = it
                        isSheetOpen = true
                    })
                }
            }
        }
    }
}

@Composable
fun AttendanceBottomAppbar(
    modifier: Modifier = Modifier, action: () -> Unit = {}
) {
    val actionList = listOf(
        ImageIconModel(imageVector = Icons.Rounded.MenuOpen,
            contentDescription = R.string.menu,
            onClick = {}),
        ImageIconModel(imageVector = Icons.Outlined.Book,
            contentDescription = R.string.add_from_Syllabus,
            onClick = {}),
        ImageIconModel(imageVector = Icons.Outlined.Archive,
            contentDescription = R.string.archive,
            onClick = {}),
        ImageIconModel(imageVector = Icons.Outlined.Settings,
            contentDescription = R.string.settings,
            onClick = {}),
    )
    BottomAppBar(modifier = modifier, actions = {
        actionList.forEach { action ->
            ImageIconButton(iconModel = action)
        }
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = action,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Outlined.Add, contentDescription = "add"
            )
        }
    })
}

@Composable
fun AttendanceScreenToolbar(
    modifier: Modifier = Modifier, state: LazyListState
) {
    Box(modifier = modifier) {
        AnimatedVisibility(
            visible = !state.isScrollingUp(),
            enter = expandHorizontally() + fadeIn(),
            exit = shrinkHorizontally() + fadeOut()
        ) {
            ShrinkToolBarCompose()
        }
        AnimatedVisibility(
            visible = state.isScrollingUp(),
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            ExpandToolbarCompose()
        }
    }
}

@Composable
fun ShrinkToolBarCompose(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(grid_1)
            .fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.dividerOrCardColor,
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(grid_2),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Goal : 80%", modifier = Modifier.padding(start = grid_1)
            )
            Spacer(modifier = Modifier.width(grid_1))
            Text(text = "😎")
            Spacer(modifier = Modifier.width(grid_1))
            Text(
                text = "Current : 75%", modifier = Modifier.padding(end = grid_1)
            )
        }
    }
}

@Composable
private fun ExpandToolbarCompose(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = grid_1, end = grid_1),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier.size(95.dp)
        ) {

            CircularProgressIndicator(
                progress = 0.8f,
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .align(Alignment.Center),
                strokeWidth = grid_1,
                strokeCap = StrokeCap.Round
            )
            CircularProgressIndicator(
                progress = 0.75f,
                modifier = Modifier
                    .padding(3.dp)
                    .size(68.dp)
                    .align(Alignment.Center),
                strokeWidth = grid_1,
                strokeCap = StrokeCap.Round
            )
            Text(
                text = "😎",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = "Goal : 80%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(grid_0_5))
            Text(
                text = "Current : 75%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AttendanceScreenPreview() {
    BITAppTheme {

    }
}
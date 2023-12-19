package com.atech.bit.ui.screens.home.screen.notice.detail.compose

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.atech.bit.R
import com.atech.bit.ui.comman.BackToolbar
import com.atech.bit.ui.comman.GridImageLayout
import com.atech.bit.ui.comman.ImageIconButton
import com.atech.bit.ui.comman.ImageLoader
import com.atech.bit.ui.comman.getImageLinkNotification
import com.atech.bit.ui.screens.home.screen.notice.NoticeViewModel
import com.atech.bit.ui.theme.grid_1
import com.atech.bit.utils.openLinks
import com.atech.core.datasource.firebase.firestore.Attach
import com.atech.core.datasource.firebase.firestore.Db
import com.atech.core.utils.getDate
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoticeDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: NoticeViewModel = hiltViewModel(),
    navController: NavController = rememberNavController()
) {
    val notice = viewModel.currentClickEvent.value
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scrollState = rememberScrollState()

    var attach by remember {
        mutableStateOf(emptyList<Attach>())
    }
    val scope = rememberCoroutineScope()
    Scaffold(modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        BackToolbar(title = "", onNavigationClick = {
            navController.navigateUp()
        }, scrollBehavior = scrollBehavior, actions = {
            if (!notice?.link.isNullOrEmpty()) ImageIconButton(icon = Icons.Outlined.Link,
                contextDes = R.string.attached_link,
                onClick = {
                    notice?.link?.openLinks(context)
                })
        })
    }) {
        if (notice == null) {
            Toast.makeText(context, "Something went wrong !!", Toast.LENGTH_SHORT).show()
            navController.navigateUp()
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = grid_1)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = notice.title ?: "",
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(modifier = Modifier.height(grid_1))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notice.sender ?: "",
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
                        text = notice.created?.getDate() ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                ImageLoader(
                    modifier = Modifier.size(
                        30.dp
                    ), imageUrl = notice.getImageLinkNotification(), isRounderCorner = true
                )
            }
            Spacer(modifier = Modifier.height(grid_1))
            Text(
                text = notice.body ?: "",
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.height(grid_1))
            scope.launch {
                viewModel.getAttach.invoke(Db.Notice,
                    notice.path!!, action = { attaches ->
                        attach = attaches
                    })
            }
            AnimatedVisibility(attach.isNotEmpty()) {
                GridImageLayout(
                    list = attach
                )
            }
        }
    }
}
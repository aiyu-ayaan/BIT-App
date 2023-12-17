package com.atech.bit.ui.comman

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.atech.bit.ui.theme.grid_1


@Composable
fun LazyListScope.stateLoadingScreen(
    modifier: Modifier = Modifier,
    isLoading: Boolean = true,
    errorMessage: String = "No Data Found",
    isHasError: Boolean = false,
    fromNetWork: Boolean = true
) = this.apply {
    if (isLoading)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(modifier),
            contentAlignment = Alignment.Center
        ) {
            item {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(80.dp),
                    strokeWidth = grid_1,
                    strokeCap = StrokeCap.Round
                )
            }
        }

    if (isHasError)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(modifier),
            contentAlignment = Alignment.Center
        ) {
            item {
                if (fromNetWork)
                    NetworkScreenEmptyScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(modifier),
                        text = errorMessage
                    )
                else
                    EmptyScreen()
            }
        }

}

@Composable
fun StateLoadingScreen(
    modifier: Modifier = Modifier,
    isLoading: Boolean = true,
    errorMessage: String = "No Data Found",
    isHasError: Boolean = false,
    fromNetWork: Boolean = true
) {
    Column {
        if (isLoading)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(modifier),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(80.dp),
                    strokeWidth = grid_1,
                    strokeCap = StrokeCap.Round
                )
            }

        if (isHasError)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(modifier),
                contentAlignment = Alignment.Center
            ) {
                if (fromNetWork)
                    NetworkScreenEmptyScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(modifier),
                        text = errorMessage
                    )
                else
                    EmptyScreen()
            }
    }
}
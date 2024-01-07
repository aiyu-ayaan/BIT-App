package com.atech.bit.ui.comman

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.atech.bit.R
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.grid_1

@Composable
fun GlobalEmptyScreen(
    modifier: Modifier = Modifier,
    isEmpty: Boolean = true,
    emptyText: String = "Empty",
    content: @Composable () -> Unit = {}
) {
    if (isEmpty)
        Column(
            modifier = modifier
                .padding(grid_1)
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty_screen))
            val progress by animateLottieCompositionAsState(
                composition,
                iterations = LottieConstants.IterateForever
            )
            LottieAnimation(
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f),
                composition = composition,
                progress = { progress },
            )
            Text(text = emptyText)
        }
    else
        content()
}

@Preview(showBackground = true)
@Composable
private fun GlobalEmptyScreenPreview() {
    BITAppTheme {
        GlobalEmptyScreen()
    }
}
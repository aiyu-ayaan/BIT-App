package com.atech.bit.ui.screens.view_image

import android.util.Log
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.atech.bit.ui.comman.BackToolbar
import com.atech.bit.ui.comman.ImageLoader
import com.atech.bit.ui.theme.BITAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewImageScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    link: String = ""
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            BackToolbar(
                title = "",
                onNavigationClick = {
                    navController.popBackStack()
                }
            )
        }
    ) {
        var scale by remember {
            mutableStateOf(1f)
        }
        var offSet by remember {
            mutableStateOf(Offset.Zero)
        }


        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            val state = rememberTransformableState { zoomChange, panChange, _ ->
                scale = (scale * zoomChange).coerceIn(1f, 5f)
                val extraWidth = (scale - 1) * constraints.maxWidth
                val extraHeight = (scale - 1) * constraints.maxHeight
                val maxX = extraWidth / 2f
                val maxY = extraHeight / 2f
                offSet = Offset(
                    x = (offSet.x + scale * panChange.x).coerceIn(-maxX, maxX),
                    y = (offSet.y + scale * panChange.y).coerceIn(-maxY, maxY)
                )
            }
            ImageLoader(
                imageUrl = link,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offSet.x
                        translationY = offSet.y
                    }
                    .transformable(state = state),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ViewImageScreenPreview() {
    BITAppTheme {
        ViewImageScreen()
    }
}
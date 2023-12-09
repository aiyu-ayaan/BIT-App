package com.atech.course.screen.course

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.atech.theme.BITAppTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CourseScreen(
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier.consumeWindowInsets(it),
            contentPadding = it
        ) {
            items(1) {
                Text(text = "Course")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CourseScreenPreview() {
    BITAppTheme {
        CourseScreen()
    }
}
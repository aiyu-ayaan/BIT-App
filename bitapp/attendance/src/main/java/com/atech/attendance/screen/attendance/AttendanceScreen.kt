package com.atech.attendance.screen.attendance

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.atech.theme.BITAppTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AttendanceScreen(
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier.consumeWindowInsets(it),
            contentPadding = it
        ) {
            items(1){
                Text(text = "Attendance")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AttendanceScreenPreview() {
    BITAppTheme {
        AttendanceScreen()
    }
}
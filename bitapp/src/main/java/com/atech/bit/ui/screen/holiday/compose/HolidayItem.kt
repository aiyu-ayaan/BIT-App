package com.atech.bit.ui.screen.holiday.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.atech.core.data_source.retrofit.model.Holiday
import com.atech.theme.BITAppTheme
import com.atech.theme.grid_1
import com.atech.theme.grid_2

@Composable
fun HolidayItem(
    modifier: Modifier = Modifier,
    holiday: Holiday
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(grid_1),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .weight(.5f)
            ) {
                Text(
                    text = holiday.date,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(grid_2)
                        .align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.width(grid_2))
            Column (
                modifier = Modifier
                    .weight(1f)
            ){
                Text(
                    text = holiday.occasion,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = holiday.day,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HolidayItemPreview() {
    BITAppTheme {
        HolidayItem(
            holiday = Holiday(
                sno = 1,
                day = "Wednesday",
                date = "03",
                occasion = "Ayaan's Birthday",
                month = "may",
                type = "Main"
            )
        )
    }
}
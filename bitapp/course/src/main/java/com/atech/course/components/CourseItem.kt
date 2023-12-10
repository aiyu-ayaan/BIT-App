package com.atech.course.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.atech.core.firebase.remote.model.CourseDetailModel
import com.atech.theme.BITAppTheme
import com.atech.theme.dividerOrCardColor
import com.atech.theme.grid_0_5
import com.atech.theme.grid_2
import com.atech.theme.grid_3

@Composable
fun CourseItem(
    modifier: Modifier = Modifier,
    details: CourseDetailModel,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick.invoke() }
    ) {
        Card(
            Modifier
                .fillMaxWidth()
                .padding(vertical = grid_0_5, horizontal = grid_2),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.dividerOrCardColor
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(grid_2),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Book,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(grid_3))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,

                    ) {
                    Text(
                        text = details.name.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = stringResource(
                            id = com.atech.theme.R.string.sem_with_sem_count,
                            details.sem
                        )
                    )
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun CourseItemPreview() {
    BITAppTheme {
        CourseItem(
            details = CourseDetailModel("BCA", 6)
        )
    }
}
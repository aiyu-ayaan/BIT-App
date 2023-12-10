package com.atech.course.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atech.theme.BITAppTheme
import com.atech.theme.dividerOrCardColor
import com.atech.theme.grid_0_5
import com.atech.theme.grid_1
import com.atech.theme.grid_2


@Composable
fun SubjectTitle(
    title: String = "Theory"
) {
    Text(
        modifier = Modifier.padding(vertical = grid_0_5, horizontal = grid_1),
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun SubjectItem(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clickable { },
    ) {
        OutlinedCard(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = grid_0_5, horizontal = grid_2),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.dividerOrCardColor)
        ) {
            Column(
                modifier = modifier.padding(grid_2)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CA101",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.width(grid_1))
                    Icon(
                        modifier = Modifier.size(18.dp),
                        imageVector = Icons.Outlined.CreditCard,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(grid_1))
                    Text(
                        text = "Credits : 3",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    text = "Programming in Java",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun SubjectItemPreview() {
    BITAppTheme {
        Column {
            SubjectTitle()
            SubjectItem()
        }
    }
}
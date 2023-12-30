package com.atech.bit.ui.comman

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.atech.bit.R
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.dividerOrCardColor
import com.atech.bit.ui.theme.grid_1
import com.atech.bit.ui.theme.grid_2
import com.atech.bit.utils.getVersion

@Composable
fun NavHeader(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column {
        Row(
            modifier = modifier
                .clickable { onClick() }
                .fillMaxWidth()
                .padding(
                    horizontal = grid_2,
                    vertical = grid_1
                ),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.by_puppeteers),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.version, getVersion()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Divider(
            color = MaterialTheme.colorScheme
                .dividerOrCardColor
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NavHeaderPreview() {
    BITAppTheme {
        NavHeader()
    }
}
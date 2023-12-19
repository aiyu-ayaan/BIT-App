package com.atech.bit.ui.comman

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.NightlightRound
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.captionColor
import com.atech.bit.ui.theme.grid_1
import com.atech.bit.ui.theme.grid_2
import com.atech.bit.ui.theme.grid_3


@Composable
internal fun PreferenceItemDescription(
    modifier: Modifier = Modifier,
    text: String,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    enabled: Boolean,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    Text(
        modifier = modifier.padding(top = 2.dp),
        text = text,
        maxLines = maxLines,
        style = style,
        color = if (enabled) color else MaterialTheme.colorScheme.captionColor,
        overflow = overflow
    )
}

@Composable
internal fun PreferenceItemTitle(
    modifier: Modifier = Modifier,
    text: String,
    maxLines: Int = 2,
    enabled: Boolean,
    color: Color = MaterialTheme.colorScheme.onSurface,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    Text(
        modifier = modifier,
        text = text,
        maxLines = maxLines,
        style = MaterialTheme.typography.titleMedium,
        color = if (enabled) color else MaterialTheme.colorScheme.captionColor,
        overflow = overflow
    )
}

@Composable
fun PreferenceItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isChecked: Boolean = true,
    checkedIcon: ImageVector = Icons.Outlined.Check,
    onClick: (() -> Unit) = {},
) {
    val thumbContent: (@Composable () -> Unit)? = if (isChecked) {
        {
            Icon(
                imageVector = checkedIcon,
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    } else {
        null
    }
    Surface(
        modifier = modifier
            .toggleable(
                value = isChecked, onValueChange = { onClick() }, enabled = enabled
            )
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(grid_2, grid_1)
                .padding(start = if (icon == null) 12.dp else 0.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.let {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = grid_1, end = grid_2)
                        .size(grid_3),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                PreferenceItemTitle(
                    text = title, enabled = enabled
                )
                if (!description.isNullOrEmpty()) PreferenceItemDescription(
                    text = description, enabled = enabled
                )
            }
            Switch(
                checked = isChecked,
                onCheckedChange = null,
                modifier = Modifier.padding(start = 20.dp, end = 6.dp),
                enabled = enabled,
                thumbContent = thumbContent
            )
        }
    }
}

@Composable
fun PreferenceItemBorder(
    title: String,
    description: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isSwitchEnabled: Boolean = enabled,
    isChecked: Boolean = true,
    checkedIcon: ImageVector = Icons.Outlined.Check,
    onClick: (() -> Unit) = {},
    onChecked: () -> Unit = {}
) {
    val thumbContent: (@Composable () -> Unit)? = if (isChecked) {
        {
            Icon(
                imageVector = checkedIcon,
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    } else {
        null
    }
    Surface(
        modifier = Modifier.clickable(
            enabled = enabled,
            onClick = onClick,
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(grid_2, grid_1)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.let {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 16.dp)
                        .size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                PreferenceItemTitle(text = title, enabled = enabled)
                if (!description.isNullOrEmpty()) PreferenceItemDescription(
                    text = description,
                    enabled = enabled
                )
            }
            Divider(
                modifier = Modifier
                    .height(32.dp)
                    .padding(horizontal = 8.dp)
                    .width(1f.dp)
                    .align(Alignment.CenterVertically),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
            Switch(
                checked = isChecked,
                onCheckedChange = { onChecked() },
                modifier = Modifier
                    .padding(horizontal = 6.dp),
                enabled = isSwitchEnabled,
                thumbContent = thumbContent
            )
        }
    }
}


@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview(showBackground = true)
@Composable
private fun PreferenceItemPreview() {
    BITAppTheme {
        PreferenceItem(
            title = "Dynamic Color",
            description = "This is description",
            icon = Icons.Outlined.NightlightRound,
        )
    }
}
package com.atech.bit.ui.comman

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.atech.bit.R

@Composable
fun ImageIconButton(
    modifier: Modifier = Modifier,
    iconModel: ImageIconModel,
    tint: Color = LocalContentColor.current,
) {
    ImageIconButton(
        modifier = modifier,
        onClick = iconModel.onClick,
        icon = iconModel.imageVector,
        contextDes = iconModel.contentDescription,
        tint = iconModel.tint ?: tint,
        isEnable = iconModel.isEnable
    )
}

@Composable
fun ImageIconButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    tint: Color = LocalContentColor.current,
    @StringRes contextDes: Int? = null,
    onClick: () -> Unit = {},
    isEnable: Boolean = true
) {
    IconButton(
        modifier = modifier,
        onClick = { onClick() },
        enabled = isEnable
    ) {
        Icon(
            imageVector = icon,
            contentDescription =
            if (contextDes == null) null else stringResource(id = contextDes),
            tint = tint
        )
    }
}

@Composable
fun ImageIconButton(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    tint: Color = LocalContentColor.current,
    @StringRes contextDes: Int? = null,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        onClick = { onClick() }
    ) {
        Icon(
            painter = painterResource(id = icon), contentDescription =
            if (contextDes == null) null else stringResource(id = contextDes),
            tint = tint
        )
    }
}

data class ImageIconModel(
    val imageVector: ImageVector,
    @StringRes val contentDescription: Int? = null,
    val onClick: () -> Unit,
    val isVisible: Boolean = true,
    val tint: Color? = null,
    val isEnable: Boolean = true
)

val backIconModel = ImageIconModel(
    imageVector = Icons.Default.ArrowBack,
    contentDescription = R.string.back,
    onClick = {}
)
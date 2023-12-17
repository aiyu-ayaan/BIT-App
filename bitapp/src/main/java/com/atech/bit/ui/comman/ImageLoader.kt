package com.atech.bit.ui.comman

import androidx.annotation.DrawableRes
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.atech.bit.R


@Composable
fun ImageLoader(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    @DrawableRes errorImage: Int = R.drawable.ic_ayaan_beta,
    contentScale: ContentScale = ContentScale.Fit,
    isRounderCorner: Boolean = false,
    onError: () -> Unit = {}
) {

    AsyncImage(
        modifier = modifier
            .let { if (isRounderCorner) it.clip(shape = CircleShape) else it },
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .allowHardware(false)
            .decoderFactory(SvgDecoder.Factory())
            .crossfade(true)
            .build(),
        contentDescription = stringResource(id = R.string.loaded_image),
        contentScale = contentScale,
        error = painterResource(id = errorImage),
        onError = {
            onError()
        }
    )
}
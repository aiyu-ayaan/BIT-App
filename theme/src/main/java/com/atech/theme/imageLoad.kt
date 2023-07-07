package com.atech.theme

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


fun ImageView.loadCircular(url: String?) = Glide.with(this)
    .load(url)
    .transition(
        withCrossFade(
            DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
        )
    )
    .placeholder(R.drawable.ic_downloading)
    .error(R.drawable.ic_running_error)
    .transform(CircleCrop())
    .into(this)

fun ImageView.loadImage(url: String?) = Glide.with(this)
    .load(url)
    .transition(
        withCrossFade(
            DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
        )
    )
    .placeholder(R.drawable.ic_downloading)
    .error(R.drawable.ic_running_error)
    .into(this)


suspend fun String.getBitMap(context: Context): Bitmap? = withContext(
    Dispatchers.IO
) {
    Glide.with(context)
        .asBitmap()
        .load(this@getBitMap)
        .submit()
        .get()
}

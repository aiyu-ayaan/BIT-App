package com.atech.theme

import android.widget.ImageView
import coil.load
import coil.transform.CircleCropTransformation


fun ImageView.loadCircular(url: String) = this.apply {
    load(url) {
        crossfade(true)
        placeholder(R.drawable.ic_downloading)
        error(R.drawable.ic_running_error)
        transformations(CircleCropTransformation())
    }
}
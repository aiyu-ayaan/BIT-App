package com.atech.theme

import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.annotation.DrawableRes
import com.atech.theme.databinding.CardViewHighlightBinding

data class CardHighlightModel(
    val title: String,
    val description: String,
    @DrawableRes
    val icon: Int,
    val onClick: () -> Unit = {},
    @DrawableRes
    val endIcon: Int? = null,
    val endIconAction: (() -> Unit)? = null,
)


fun CardViewHighlightBinding.set(
    data: CardHighlightModel
) = this.apply {
    textViewTitle.text = data.title
    textViewDes.text = data.description
    imageViewIcon.setImageResource(data.icon)
    imageViewVisibility.apply {
        visibility = if (data.endIcon != null) VISIBLE else GONE
        data.endIcon?.let { setImageResource(it) }
        setOnClickListener { data.endIconAction?.invoke() }
    }
    root.setOnClickListener { data.onClick() }
}
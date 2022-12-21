package com.atech.bit.utils

import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.annotation.DrawableRes
import com.atech.bit.databinding.CardViewHighlightBinding

data class CardViewHighlightContent(
    val title: String,
    val description: String,
    @DrawableRes
    val icon: Int,
    val isVisibilityIconVisible: Boolean = false
)


fun CardViewHighlightBinding.bindData(
    data: CardViewHighlightContent,
    onVisibilityClick: (CardViewHighlightContent) -> Unit = {},
    action: (CardViewHighlightContent) -> Unit = {}
) = this.apply {
    textViewTitle.text = data.title
    textViewDes.text = data.description
    imageViewIcon.setImageResource(data.icon)
    imageViewVisibility.visibility = if (data.isVisibilityIconVisible) VISIBLE else GONE
    root.setOnClickListener { action(data) }
    imageViewVisibility.setOnClickListener { onVisibilityClick(data) }
}
package com.atech.bit.ui.fragments.search.state

import com.google.errorprone.annotations.Keep

@Keep
data class SearchTextFieldState(
    val text: String = "",
    val hint: String = "",
    val isHintVisible: Boolean = true
)
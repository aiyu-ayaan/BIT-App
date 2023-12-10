package com.atech.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.atech.theme.bottom_nav_height

@Composable
fun BottomPadding() {
    Spacer(modifier = Modifier.height(bottom_nav_height))
}


fun LazyListScope.singleElement(
    modifier: Modifier = Modifier,
    key : Any? = null,
    content: @Composable LazyListScope.() -> Unit = { BottomPadding() }
) = this.apply {
    item(key = key) {
        this@singleElement.content()
    }
}

/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.comman

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.atech.bit.ui.theme.bottom_nav_height

@Composable
fun BottomPadding() {
    Spacer(modifier = Modifier.height(bottom_nav_height))
}


fun LazyListScope.singleElement(
    key: Any? = null,
    content: @Composable LazyListScope.() -> Unit = { BottomPadding() }
) = this.apply {
    item(key = key) {
        this@singleElement.content()
    }
}

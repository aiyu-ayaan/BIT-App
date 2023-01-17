package com.atech.bit.ui.fragments.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.atech.bit.R
import com.atech.bit.ui.fragments.search.state.SearchTextFieldState
import com.atech.bit.utils.getComposeColor

@Composable
fun SearchBarComponent(
    modifier: Modifier = Modifier,
    state: SearchTextFieldState,
    onValueChange: (String) -> Unit = {},
    onFocusChange: (FocusState) -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = LocalContext.current.getComposeColor(R.attr.bottomBar),
        )
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
            Spacer(modifier = Modifier.width(16.dp))
            TransparentHiltTextField(
                modifier = Modifier.fillMaxWidth(),
                text = state.text,
                hint = state.hint,
                onValueChange = onValueChange,
                onFocusChange = onFocusChange,
                textStyle = MaterialTheme.typography.titleMedium,
                isHintVisible = state.isHintVisible
            )
        }
    }
}

@Composable
fun TransparentHiltTextField(
    text: String,
    hint: String,
    modifier: Modifier = Modifier,
    isHintVisible: Boolean = true,
    onValueChange: (String) -> Unit,
    textStyle: TextStyle = TextStyle(),
    singleLine: Boolean = false,
    onFocusChange: (FocusState) -> Unit
) {
    Box(modifier) {
        BasicTextField(
            value = text,
            onValueChange = onValueChange,
            textStyle = textStyle.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            singleLine = singleLine,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { onFocusChange(it) })
        if (isHintVisible) {
            Text(
                text = hint,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


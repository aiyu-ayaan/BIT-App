package com.atech.chat.comman

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.CodeBlockStyle
import com.halilibo.richtext.ui.RichTextStyle
import com.halilibo.richtext.ui.material3.Material3RichText
import com.halilibo.richtext.ui.string.RichTextStringStyle

@Composable
fun MarkDown(
    modifier: Modifier = Modifier,
    markDown: String,
    codeBlockBackBackground: Color = Color.Gray,
    linkTextColor: Color = MaterialTheme.colorScheme.primary
) {
    Material3RichText(
        modifier = modifier,
        style = RichTextStyle.Default
            .copy(
                codeBlockStyle = CodeBlockStyle.Default
                    .copy(
                        modifier = Modifier
                            .background(color = Color.Black, shape = MaterialTheme.shapes.small),
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            color = Color.White
                        ),
                        wordWrap = true,
                        padding = 8.sp
                    ),
                stringStyle = RichTextStringStyle.Default.copy(
                    linkStyle = SpanStyle(
                        textDecoration = TextDecoration.Underline,
                        color = linkTextColor
                    ),
                    codeStyle = SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium,
                        background = codeBlockBackBackground
                    ),
                )
            )
    ) {
        Markdown(content = markDown)
    }
}
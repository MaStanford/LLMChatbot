package com.stanford.chatapp.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier
)
{
    val annotatedString = buildAnnotatedString {
        val markdown = text.replace("\\n", "\n")
        var i = 0
        while (i < markdown.length) {
            when {
                markdown.startsWith("**", i) -> {
                    val end = markdown.indexOf("**", i + 2)
                    if (end != -1) {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(markdown.substring(i + 2, end))
                        }
                        i = end + 2
                    } else {
                        append(markdown[i])
                        i++
                    }
                }
                markdown.startsWith("*", i) -> {
                    val end = markdown.indexOf("*", i + 1)
                    if (end != -1) {
                        withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                            append(markdown.substring(i + 1, end))
                        }
                        i = end + 1
                    } else {
                        append(markdown[i])
                        i++
                    }
                }
                markdown.startsWith("> ", i) -> {
                    val end = markdown.indexOf("\n", i + 2)
                    if (end != -1) {
                        withStyle(style = SpanStyle(color = Color.Gray)) {
                            append(markdown.substring(i + 2, end))
                        }
                        i = end
                    } else {
                        withStyle(style = SpanStyle(color = Color.Gray)) {
                            append(markdown.substring(i + 2))
                        }
                        i = markdown.length
                    }
                }
                markdown.startsWith("\\", i) -> {
                    if (i + 1 < markdown.length) {
                        append(markdown[i + 1])
                        i += 2
                    } else {
                        append(markdown[i])
                        i++
                    }
                }
                else -> {
                    append(markdown[i])
                    i++
                }
            }
        }
    }

    Text(
        text = annotatedString,
        modifier = modifier
    )
}

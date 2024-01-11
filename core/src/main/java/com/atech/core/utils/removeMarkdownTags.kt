/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.core.utils

fun String.convertMarkDownToText(): String {
    var text = this

    // Remove links and images
    text = text.replace(Regex("""\[(.*?)\]\(.*?\)"""), "$1")
    text = text.replace(Regex("""!\[(.*?)\]\(.*?\)"""), "$1")

    // Remove headings
    text = text.replace(Regex("""^#+\s+(.*?)\s*$""", RegexOption.MULTILINE), "$1")

    // Remove emphasis and strong emphasis
    text = text.replace(Regex("""\*\*(.*?)\*\*"""), "$1")
    text = text.replace(Regex("""__(.*?)__"""), "$1")
    text = text.replace(Regex("""\*(.*?)\*"""), "$1")
    text = text.replace(Regex("""_(.*?)_"""), "$1")

    // Remove strikethrough
    text = text.replace(Regex("""~~(.*?)~~"""), "$1")

    // Remove inline code
    text = text.replace(Regex("""`([^`]+)`"""), "$1")

    // Remove block code
    text = text.replace(Regex("""```.*?```""", RegexOption.DOT_MATCHES_ALL), "")

    // Remove blockquotes
    text = text.replace(Regex("""^\s*>\s+(.*?)\s*$""", RegexOption.MULTILINE), "$1")

    // Remove lists
    text = text.replace(Regex("""^\s*[\*\+-]\s+(.*?)\s*$""", RegexOption.MULTILINE), "$1")
    text = text.replace(Regex("""^\s*\d+\.\s+(.*?)\s*$""", RegexOption.MULTILINE), "$1")

    // Extract table cell content
    text = text.replace(Regex("""\|([^|]+)\|"""), "$1")

    // Remove horizontal lines
    text = text.replace(Regex("""^\s*[-*_]{3,}\s*$""", RegexOption.MULTILINE), "")

    // Trim leading and trailing whitespaces
    return text.trim()
}
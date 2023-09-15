package org.crystal.intellij.util

import com.intellij.openapi.util.text.StringUtil

fun String.deparenthesize(): String {
    return if (length >= 2 && first() == '(' && last() == ')') substring(1, length - 1) else this
}

fun String.unquote(): String {
    return StringUtil.unquoteString(this)
}

fun String.countLeadingSpaces(offset: Int): Int {
    var count = 0
    for (i in offset until length) {
        val ch = this[i]
        if (ch == ' ' || ch == '\t') count++ else break
    }
    return count
}
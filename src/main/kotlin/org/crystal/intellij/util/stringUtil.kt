package org.crystal.intellij.util

import com.intellij.openapi.util.text.StringUtil

fun String.deparenthesize(): String {
    return if (length >= 2 && first() == '(' && last() == ')') substring(1, length - 1) else this
}

fun String.unquote(): String {
    return StringUtil.unquoteString(this)
}
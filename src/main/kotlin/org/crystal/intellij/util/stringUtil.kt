package org.crystal.intellij.util

fun String.deparenthesize(): String {
    return if (length >= 2 && first() == '(' && last() == ')') substring(1, length - 1) else this
}
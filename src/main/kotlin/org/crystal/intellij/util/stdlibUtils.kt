package org.crystal.intellij.util

import com.intellij.util.containers.JBIterable

inline fun <reified T> JBIterable<*>.firstInstanceOrNull(): T? {
    for (child in this) {
        if (child is T) return child
    }
    return null
}

fun StringBuilder.appendSpaced(text: String): StringBuilder {
    if (isNotEmpty() && last() != ' ') append(' ')
    return append(text)
}

fun <T, A : Appendable> A.append(
    iterable: Iterable<T>,
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    appendElement: A.(T) -> Unit
): A {
    append(prefix)
    for ((count, element) in iterable.withIndex()) {
        if (count + 1 > 1) append(separator)
        appendElement(element)
    }
    append(postfix)
    return this
}
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
    iterable: Iterable<T>?,
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    appendElement: A.(Int, T) -> Unit
): A {
    if (iterable == null) return this
    append(prefix)
    for ((i, element) in iterable.withIndex()) {
        if (i > 0) append(separator)
        appendElement(i, element)
    }
    append(postfix)
    return this
}

fun <T, A : Appendable> A.append(
    sequence: Sequence<T>?,
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    appendElement: A.(T) -> Unit
): A {
    if (sequence == null) return this
    append(prefix)
    for ((count, element) in sequence.withIndex()) {
        if (count + 1 > 1) append(separator)
        appendElement(element)
    }
    append(postfix)
    return this
}

private fun <T> List<T>.binaryIndexOf(condition: (T) -> Boolean): Int {
    var from = 0
    var to = size

    var result = -1

    while (from < to) {
        val mid = (from + to) ushr 1
        if (condition(get(mid))) {
            result = mid
            to = mid
        }
        else {
            from = mid + 1
        }
    }

    return result
}

fun <T> List<T>.binaryFirstOrNull(condition: (T) -> Boolean): T? {
    return getOrNull(binaryIndexOf(condition))
}

inline fun <reified T> Any?.cast() = this as T
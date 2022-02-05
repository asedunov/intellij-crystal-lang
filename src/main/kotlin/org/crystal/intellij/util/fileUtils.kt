package org.crystal.intellij.util

import java.nio.file.InvalidPathException
import java.nio.file.Paths

fun String.toPathOrNull() = try {
    Paths.get(this)
} catch (e: InvalidPathException) {
    null
}
package org.crystal.intellij.util

import com.intellij.util.io.exists
import com.intellij.util.io.isFile
import java.nio.file.InvalidPathException
import java.nio.file.Path
import java.nio.file.Paths

private val emptyPath: Path = Path.of("")

fun String.toPathOrNull() = try {
    Paths.get(this)
} catch (e: InvalidPathException) {
    null
}

fun String.toPathOrEmpty() = toPathOrNull() ?: emptyPath

val Path.isValidFile: Boolean
    get() = exists() && isFile()
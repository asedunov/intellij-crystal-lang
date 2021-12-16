package org.crystal.intellij.util

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager

operator fun VirtualFile.get(name: String) = when (name) {
    "." -> this
    ".." -> parent
    else -> findChild(name)
}

fun VirtualFile.toPsi(manager: PsiManager) =
    if (isDirectory) manager.findDirectory(this) else manager.findFile(this)
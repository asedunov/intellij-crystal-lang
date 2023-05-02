package org.crystal.intellij.util

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiUtilCore
import java.nio.file.Path

operator fun VirtualFile.get(name: String) = when (name) {
    "." -> this
    ".." -> parent
    else -> findChild(name)
}

fun VirtualFile.toPsi(manager: PsiManager) =
    if (isDirectory) manager.findDirectory(this) else manager.findFile(this)

fun VirtualFile.toPsi(project: Project) = toPsi(PsiManager.getInstance(project))

fun VirtualFile.isAncestor(file: VirtualFile, strict: Boolean = true): Boolean {
    return VfsUtil.isAncestor(this, file, strict)
}

val PsiElement.virtualFile: VirtualFile?
    get() = PsiUtilCore.getVirtualFile(this)

fun VirtualFile.fullRefresh(async: Boolean) {
    VfsUtil.markDirtyAndRefresh(async, true, true, this)
}

fun Path.findVirtualFile(refreshIfNeeded: Boolean) = VfsUtil.findFile(this, refreshIfNeeded)
package org.crystal.intellij.util

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiUtilCore

operator fun VirtualFile.get(name: String) = when (name) {
    "." -> this
    ".." -> parent
    else -> findChild(name)
}

fun VirtualFile.toPsi(manager: PsiManager) =
    if (isDirectory) manager.findDirectory(this) else manager.findFile(this)

fun VirtualFile.toPsi(project: Project) = toPsi(PsiManager.getInstance(project))

val PsiElement.virtualFile: VirtualFile?
    get() = PsiUtilCore.getVirtualFile(this)

fun VirtualFile.fullRefresh() {
    VfsUtil.markDirtyAndRefresh(false, true, true, this)
}
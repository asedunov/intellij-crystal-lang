package org.crystal.intellij.util

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiUtilCore
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.jetbrains.annotations.SystemIndependent
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.pathString

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

val VirtualFile.isFile: Boolean
    get() = isValid && !isDirectory

@RequiresReadLock
fun VirtualFile.findFileOrDirectory(relativePath: @SystemIndependent String): VirtualFile? {
    var virtualFile = checkNotNull(fileSystem.findFileByPath("/")) {
        "Cannot find file system root for file: $path/$relativePath"
    }
    val names = path.toNioPath().getResolvedPath(relativePath).pathList
    for (name in names) {
        virtualFile = virtualFile.findChild(name) ?: return null
    }
    return virtualFile
}

@RequiresReadLock
fun VirtualFile.findFile(relativePath: @SystemIndependent String): VirtualFile? {
    val file = findFileOrDirectory(relativePath) ?: return null
    if (!file.isFile) {
        throw IOException("Expected file instead of directory: $path/$relativePath")
    }
    return file
}

fun String.toNioPath(): Path {
    return Paths.get(FileUtil.toSystemDependentName(this))
}

fun Path.getResolvedPath(relativePath: String): Path {
    return resolve(FileUtil.toSystemDependentName(relativePath)).normalize()
}

val Path.pathList: List<String>
    get() = map { it.pathString }
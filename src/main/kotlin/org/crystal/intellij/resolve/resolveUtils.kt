package org.crystal.intellij.resolve

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.rootManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.PsiManager
import com.intellij.psi.util.parents
import com.intellij.util.containers.addIfNotNull
import org.crystal.intellij.config.stdlibRootPsiDirectory

fun Module.crystalPathRoots(): List<PsiFileSystemItem> {
    val contexts = ArrayList<PsiFileSystemItem>()
    val psiManager = PsiManager.getInstance(project)
    rootManager.contentRoots.flatMapTo(contexts) {
        val dir = psiManager.findDirectory(it) ?: return@flatMapTo emptyList()
        listOfNotNull(dir, dir.findSubdirectory("src"), dir.findSubdirectory("lib"))
    }
    contexts.addIfNotNull(project.stdlibRootPsiDirectory)
    return contexts
}

fun Project.crystalPathRoots() = listOfNotNull(stdlibRootPsiDirectory)

fun PsiFile.isCrystalLibraryFile(): Boolean {
    val root = project.stdlibRootPsiDirectory ?: return false
    return containingDirectory.parents(true).any { it == root }
}
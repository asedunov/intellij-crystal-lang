package org.crystal.intellij.resolve

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.rootManager
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.PsiManager
import com.intellij.util.containers.addIfNotNull
import org.crystal.intellij.config.crystalWorkspaceSettings
import org.crystal.intellij.util.toPsi

fun Module.crystalPathRoots(): List<PsiFileSystemItem> {
    val contexts = ArrayList<PsiFileSystemItem>()
    val psiManager = PsiManager.getInstance(project)
    rootManager.contentRoots.flatMapTo(contexts) {
        val dir = psiManager.findDirectory(it) ?: return@flatMapTo emptyList()
        listOfNotNull(dir, dir.findSubdirectory("src"), dir.findSubdirectory("lib"))
    }
    contexts.addIfNotNull(project.crystalWorkspaceSettings.stdlibRootDirectory?.toPsi(psiManager))
    return contexts
}

fun Project.crystalPathRoots(): List<PsiFileSystemItem> {
    val psiManager = PsiManager.getInstance(this)
    return listOfNotNull(crystalWorkspaceSettings.stdlibRootDirectory?.toPsi(psiManager))
}
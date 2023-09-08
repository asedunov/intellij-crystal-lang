package org.crystal.intellij.lang.resolve

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.rootManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.PsiManager
import com.intellij.psi.util.parents
import com.intellij.util.containers.addIfNotNull
import org.crystal.intellij.lang.config.stdlibRootPsiDirectory
import org.crystal.intellij.lang.psi.*
import org.crystal.intellij.lang.resolve.symbols.CrConstantLikeSym
import org.crystal.intellij.lang.resolve.symbols.CrModuleLikeSym

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

fun CrElement.currentModuleLikeSym(): CrModuleLikeSym? {
    val typeDef = parentStubOrPsiOfType<CrTypeDefinition>()
    return if (typeDef != null) typeDef.resolveSymbol() as? CrModuleLikeSym else project.resolveFacade.program
}

inline fun <reified S : CrConstantLikeSym<*>> CrConstantName.resolveAs(): S? {
    val path = parent as? CrPathNameElement ?: return null
    val typeDef = path.parent as? CrTypeDefinition ?: return null
    return typeDef.resolveSymbol() as? S
}
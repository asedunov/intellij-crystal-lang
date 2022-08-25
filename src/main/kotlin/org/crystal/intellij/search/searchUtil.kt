package org.crystal.intellij.search

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.SearchScope
import com.intellij.psi.stubs.AbstractStubIndex
import com.intellij.psi.util.isAncestor
import org.crystal.intellij.resolve.symbols.CrMacroSym
import org.crystal.intellij.resolve.symbols.CrModuleLikeSym

operator fun LocalSearchScope.contains(e: PsiElement) = scope.any { it.isAncestor(e) }

fun <Key : Any, Psi : PsiElement> AbstractStubIndex<Key, Psi>.get(key: Key, project: Project, searchScope: SearchScope): Collection<Psi> {
    val indexSearchScope = when (searchScope) {
        is GlobalSearchScope -> searchScope
        is LocalSearchScope -> GlobalSearchScope.filesScope(project, searchScope.virtualFiles.asList())
        else -> return emptyList()
    }
    val candidates = get(key, project, indexSearchScope)
    if (searchScope is LocalSearchScope) {
        return candidates.filter { it in searchScope }
    }
    return candidates
}

fun CrModuleLikeSym.hasInheritors(): Boolean {
    return CrystalInheritorsSearch.search(this, false).firstOrNull() != null
}

fun CrMacroSym.hasOverrides(): Boolean {
    return CrystalOverridingMacroSearch.search(this).firstOrNull() != null
}
package org.crystal.intellij.search

import com.intellij.psi.PsiElement
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.util.isAncestor
import org.crystal.intellij.resolve.symbols.CrMacroSym
import org.crystal.intellij.resolve.symbols.CrModuleLikeSym

operator fun LocalSearchScope.contains(e: PsiElement) = scope.any { it.isAncestor(e) }

fun CrModuleLikeSym.hasInheritors(): Boolean {
    return CrystalInheritorsSearch.search(this, false).firstOrNull() != null
}

fun CrMacroSym.hasOverrides(): Boolean {
    return CrystalOverridingMacroSearch.search(this).firstOrNull() != null
}
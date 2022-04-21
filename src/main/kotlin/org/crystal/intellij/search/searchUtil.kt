package org.crystal.intellij.search

import org.crystal.intellij.resolve.symbols.CrModuleLikeSym

fun CrModuleLikeSym.hasInheritors(): Boolean {
    return CrystalInheritorsSearch.search(this, false).firstOrNull() != null
}
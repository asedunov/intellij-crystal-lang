package org.crystal.intellij.lang.resolve.symbols

import org.crystal.intellij.lang.resolve.CrStdFqNames
import org.crystal.intellij.lang.resolve.cache.newResolveSlice
import org.crystal.intellij.lang.resolve.cache.resolveCache
import org.crystal.intellij.lang.resolve.predefinedAbstractClasses
import org.crystal.intellij.lang.resolve.predefinedSuperClasses
import org.crystal.intellij.lang.resolve.scopes.getTypeAs
import org.crystal.intellij.lang.resolve.superlessClasses
import org.crystal.intellij.lang.psi.CrConstantSource
import org.crystal.intellij.lang.psi.CrSuperTypeAware
import org.crystal.intellij.lang.psi.CrTypeDefinition

sealed class CrClassLikeSym(
    name: String,
    sources: List<CrConstantSource>
) : CrModuleLikeSym(name, sources) {
    companion object {
        private val SUPER_CLASS = newResolveSlice<CrClassLikeSym, CrClassLikeSym>("SUPER_CLASS")
    }

    override val superClass: CrClassLikeSym?
        get() = program.project.resolveCache.getOrCompute(SUPER_CLASS, this) {
            if (fqName in superlessClasses) return@getOrCompute null
            predefinedSuperClasses[fqName]?.let { return@getOrCompute program.memberScope.getTypeAs(it) }
            computeSuperClass()
        }

    protected open fun computeSuperClass(): CrClassLikeSym? {
        val superClause = (sources.firstOrNull() as? CrSuperTypeAware)?.superTypeClause
        if (superClause != null) {
            return superClause.resolveSymbol() as? CrClassLikeSym
        }
        val superName = when (this) {
            is CrClassSym -> CrStdFqNames.REFERENCE
            is CrStructSym -> CrStdFqNames.STRUCT
            is CrEnumSym -> CrStdFqNames.ENUM
            else -> return null
        }
        return program.memberScope.getTypeAs(superName)
    }

    open val isAbstract: Boolean
        get() = fqName in predefinedAbstractClasses || (sources.firstOrNull() as? CrTypeDefinition)?.isAbstract == true
}
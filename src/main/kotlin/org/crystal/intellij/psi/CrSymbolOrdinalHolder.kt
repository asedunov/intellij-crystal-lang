package org.crystal.intellij.psi

import com.intellij.psi.PsiElement
import org.crystal.intellij.resolve.cache.newResolveSlice
import org.crystal.intellij.resolve.cache.resolveCache
import org.crystal.intellij.resolve.symbols.CrSymbolOrdinal

sealed interface CrSymbolOrdinalHolder : CrElement {
    fun ordinal(): CrSymbolOrdinal? = project.resolveCache.getOrCompute(ORDINAL, this) {
        val parent = parentStubOrPsiOfType<CrSymbolOrdinalHolder>() ?: return@getOrCompute null
        var index = parent.indexOfChildStub(this)
        if (index < 0) {
            index = parent
                .traverser()
                .expandAndSkip {
                    it == parent
                            || it is CrBody
                            || it is CrTypeParameterList
                            || it is CrParenthesizedExpression
                            || it is CrBlockExpression }
                .filter(CrStubbedElementImpl::class.java)
                .indexOf<PsiElement>(this)
        }
        if (index < 0) return@getOrCompute null
        val parentOrdinal = parent.ordinal() ?: return@getOrCompute null
        CrSymbolOrdinal(index, parentOrdinal)
    }
}

private val ORDINAL = newResolveSlice<CrSymbolOrdinalHolder, CrSymbolOrdinal>("ORDINAL")
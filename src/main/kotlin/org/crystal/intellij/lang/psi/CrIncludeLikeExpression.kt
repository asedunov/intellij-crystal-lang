package org.crystal.intellij.lang.psi

import com.intellij.util.SmartFMap
import org.crystal.intellij.lang.resolve.cache.newResolveSlice
import org.crystal.intellij.lang.resolve.cache.resolveCache
import org.crystal.intellij.lang.resolve.currentModuleLikeSym
import org.crystal.intellij.lang.resolve.resolveFacade
import org.crystal.intellij.lang.resolve.scopes.CrModuleLikeScope
import org.crystal.intellij.lang.resolve.symbols.CrModuleSym
import org.crystal.intellij.lang.resolve.symbols.CrSym
import org.crystal.intellij.lang.stubs.api.CrIncludeStub

sealed interface CrIncludeLikeExpression : CrExpression, CrSymbolOrdinalHolder {
    val type: CrTypeElement<*>?
        get() = stubChildOfType()

    val pathResolveScope: CrModuleLikeScope?
        get() {
            val facade = project.resolveFacade
            return facade.resolveCache.getOrCompute(RESOLUTION_SCOPE, this) {
                val prevInclude = prevInclude
                if (prevInclude != null) {
                    val prevScope = prevInclude.pathResolveScope ?: return@getOrCompute null
                    val targetModule = prevInclude.targetModule ?: return@getOrCompute prevScope
                    CrModuleLikeScope(prevScope.symbol,
                        CrModuleLikeScope.ParentList(targetModule, prevScope.parentList)
                    )
                } else {
                    currentModuleLikeSym()?.noIncludesMemberScope as? CrModuleLikeScope
                }
            }
        }

    val targetSymbol: CrSym<*>?
        get() {
            var type = type
            if (type is CrInstantiatedTypeElement) type = type.constructorType
            return (type as? CrPathTypeElement)?.path?.resolveSymbol()
        }

    val targetModule: CrModuleSym?
        get() = targetSymbol as? CrModuleSym
}

private val RESOLUTION_SCOPE = newResolveSlice<CrIncludeLikeExpression, CrModuleLikeScope>("RESOLUTION_SCOPE")
private val PREV_INCLUDES = newResolveSlice<CrElement, Map<CrIncludeExpression, CrIncludeExpression>>("PREV_INCLUDES")

private val CrIncludeLikeExpression.prevInclude: CrIncludeExpression?
    get() = parentStubOrPsiOfType<CrSymbolOrdinalHolder>()?.prevIncludes?.get(this)

private val CrSymbolOrdinalHolder.prevIncludes: Map<CrIncludeExpression, CrIncludeExpression>
    get() = project.resolveCache.getOrCompute(PREV_INCLUDES, this) {
        var map = SmartFMap.emptyMap<CrIncludeExpression, CrIncludeExpression>()
        val stub = smartStub
        val includes =
            stub?.childrenStubs?.mapNotNull { (it as? CrIncludeStub)?.psi }
                ?: traverser()
                    .expandAndSkip { it == this || it is CrBody || it is CrParenthesizedExpression || it is CrBlockExpression }
                    .filter(CrIncludeExpression::class.java)
        var lastInclude: CrIncludeExpression? = null
        for (include in includes) {
            if (lastInclude != null) {
                map = map.plus(include, lastInclude)
            }
            lastInclude = include
        }
        map
    } ?: emptyMap()
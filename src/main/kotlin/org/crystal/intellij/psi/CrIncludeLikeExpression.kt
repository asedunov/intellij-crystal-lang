package org.crystal.intellij.psi

import com.intellij.util.SmartFMap
import org.crystal.intellij.resolve.cache.newResolveSlice
import org.crystal.intellij.resolve.cache.resolveCache
import org.crystal.intellij.resolve.resolveFacade
import org.crystal.intellij.resolve.scopes.CrModuleLikeScope
import org.crystal.intellij.resolve.symbols.CrModuleLikeSym
import org.crystal.intellij.resolve.symbols.CrModuleSym
import org.crystal.intellij.stubs.api.CrIncludeStub

sealed interface CrIncludeLikeExpression : CrExpression, CrSymbolOrdinalHolder {
    val type: CrType?
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
                    val typeDef = parentStubOrPsiOfType<CrTypeDefinition>()
                    val module = if (typeDef != null) typeDef.resolveSymbol() as? CrModuleLikeSym else facade.program
                    module?.noIncludesMemberScope
                }
            }
        }

    val targetModule: CrModuleSym?
        get() {
            var type = type
            if (type is CrInstantiatedType) type = type.constructorType
            return (type as? CrPathType)?.path?.resolveSymbol() as? CrModuleSym
        }
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
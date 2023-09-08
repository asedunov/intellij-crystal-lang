package org.crystal.intellij.lang.resolve.symbols

import com.intellij.util.SmartList
import org.crystal.intellij.lang.psi.*
import org.crystal.intellij.lang.resolve.cache.newResolveSlice
import org.crystal.intellij.lang.resolve.cache.resolveCache
import org.crystal.intellij.lang.resolve.layout
import org.crystal.intellij.lang.resolve.scopes.CrEmptyScope
import org.crystal.intellij.lang.resolve.scopes.CrModuleLikeScope
import org.crystal.intellij.lang.resolve.scopes.CrScope

sealed class CrModuleLikeSym(
    name: String,
    sources: List<CrConstantSource>
) : CrProperTypeSym(name, sources) {
    companion object {
        private val NO_INCLUDES_PARENTS = newResolveSlice<CrModuleLikeSym, CrModuleLikeScope.ParentList>("NO_INCLUDES_PARENTS")
        private val NO_INCLUDES_MEMBER_SCOPE = newResolveSlice<CrModuleLikeSym, CrModuleLikeScope>("NO_INCLUDES_MEMBER_SCOPE")
        private val MEMBER_SCOPE = newResolveSlice<CrModuleLikeSym, CrModuleLikeScope>("MEMBER_SCOPE")
        private val TYPE_PARAMETERS = newResolveSlice<CrModuleLikeSym, Map<String, CrTypeParameterSym>>("TYPE_PARAMETERS")
        private val PARENTS = newResolveSlice<CrModuleLikeSym, CrModuleLikeScope.ParentList>("PARENTS")
    }

    override val containedScope: CrScope
        get() = memberScope

    open val memberScope: CrScope
        get() = program.project.resolveCache.getOrCompute(MEMBER_SCOPE, this) {
            CrModuleLikeScope(this, parents)
        } ?: CrEmptyScope

    open val superClass: CrClassLikeSym?
        get() = null

    override val parents: CrModuleLikeScope.ParentList?
        get() = program.project.resolveCache.getOrCompute(PARENTS, this) {
            computeIncludedModules().fold(noIncludesParents) { p, m -> CrModuleLikeScope.ParentList(m, p) }
        }

    protected open fun computeIncludedModules(): Collection<CrModuleLikeSym> {
        val modules = LinkedHashSet<CrModuleSym>()
        for (expr in program.layout.getIncludeLikeSources(fqName)) {
            if (expr !is CrIncludeExpression) continue
            val module = expr.targetModule ?: continue
            modules += module
        }
        return modules
    }

    val noIncludesMemberScope: CrScope
        get() = program.project.resolveCache.getOrCompute(NO_INCLUDES_MEMBER_SCOPE, this) {
            CrModuleLikeScope(this, noIncludesParents)
        } ?: CrEmptyScope

    private val noIncludesParents: CrModuleLikeScope.ParentList?
        get() = program.project.resolveCache.getOrCompute(NO_INCLUDES_PARENTS, this) {
            superClass?.let { CrModuleLikeScope.ParentList(it, null) }
        }

    private val _typeParameters: Map<String, CrTypeParameterSym>
        get() = program.project.resolveCache.getOrCompute(TYPE_PARAMETERS, this) {
            val curTypeParameterSources = (sources.firstOrNull() as? CrTypeParameterHolder)?.typeParameterList?.elements
                ?: return@getOrCompute null
            val typeParameterSources = LinkedHashMap<String, SmartList<CrTypeParameter>>()
            for (curSource in curTypeParameterSources) {
                val name = curSource.name ?: continue
                typeParameterSources.getOrPut(name, ::SmartList).add(curSource)
            }
            typeParameterSources.mapValues { (name, sources) ->
                CrTypeParameterSym(name, sources, this)
            }
        } ?: emptyMap()

    val typeParameters: Collection<CrTypeParameterSym>
        get() = _typeParameters.values

    val isGeneric: Boolean
        get() = _typeParameters.isNotEmpty()

    fun getTypeParameter(name: String): CrTypeParameterSym? = _typeParameters[name]
}
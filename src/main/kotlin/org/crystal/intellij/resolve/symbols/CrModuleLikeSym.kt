package org.crystal.intellij.resolve.symbols

import com.intellij.util.SmartList
import org.crystal.intellij.psi.CrModuleLikeDefinition
import org.crystal.intellij.psi.CrTypeParameter
import org.crystal.intellij.psi.CrTypeParameterHolder
import org.crystal.intellij.psi.CrConstantSource
import org.crystal.intellij.resolve.cache.newResolveSlice
import org.crystal.intellij.resolve.cache.resolveCache
import org.crystal.intellij.resolve.scopes.CrModuleLikeScope
import org.crystal.intellij.resolve.scopes.CrScope

sealed class CrModuleLikeSym(
    name: String,
    sources: List<CrConstantSource>
) : CrTypeSym(name, sources) {
    companion object {
        private val TYPE_PARAMETERS = newResolveSlice<CrModuleLikeSym, Map<String, CrTypeParameterSym>>("TYPE_PARAMETERS")
        private val PARENTS = newResolveSlice<CrModuleLikeSym, CrModuleLikeScope.ParentList>("PARENTS")
    }

    override val containedScope: CrScope
        get() = memberScope

    open val memberScope by lazy {
        CrModuleLikeScope(this, parents)
    }

    open val superClass: CrClassLikeSym?
        get() = null

    val parents: CrModuleLikeScope.ParentList?
        get() = program.project.resolveCache.getOrCompute(PARENTS, this) {
            computeIncludedModules().fold(noIncludesParents) { p, m -> CrModuleLikeScope.ParentList(m, p) }
        }

    private fun computeIncludedModules(): Collection<CrModuleLikeSym> {
        val modules = LinkedHashSet<CrModuleSym>()
        for (source in sources) {
            if (source !is CrModuleLikeDefinition<*, *>) continue
            for (include in source.includes) {
                val module = include.targetModule ?: continue
                modules += module
            }
        }
        return modules
    }

    val noIncludesMemberScope by lazy {
        CrModuleLikeScope(this, noIncludesParents)
    }

    private val noIncludesParents by lazy {
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
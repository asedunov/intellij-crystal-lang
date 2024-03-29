package org.crystal.intellij.lang.resolve.symbols

import org.crystal.intellij.lang.psi.CrMethod
import org.crystal.intellij.lang.resolve.cache.newResolveSlice
import org.crystal.intellij.lang.resolve.cache.resolveCache
import org.crystal.intellij.lang.resolve.scopes.CrMethodScope

@Suppress("UnstableApiUsage")
class CrMethodSym(
    name: String,
    override val namespace: CrModuleLikeSym,
    source: CrMethod
) : CrSym<CrMethod>(name, listOf(source)) {
    companion object {
        private val TYPE_PARAMETERS = newResolveSlice<CrMethodSym, Map<String, CrTypeParameterSym>>("TYPE_PARAMETERS")
    }

    override val program: CrProgramSym
        get() = namespace.program

    val source: CrMethod
        get() = sources.single()

    override val containedScope = CrMethodScope(this)

    private val _typeParameters: Map<String, CrTypeParameterSym>
        get() = program.project.resolveCache.getOrCompute(TYPE_PARAMETERS, this) {
            val typeParameterSources = source.typeParameterList?.elements ?: return@getOrCompute null
            val result = LinkedHashMap<String, CrTypeParameterSym>()
            for (typeParameterSource in typeParameterSources) {
                val name = typeParameterSource.name ?: continue
                val typeParameterSym = CrTypeParameterSym(name, listOf(typeParameterSource), this)
                result[name] = typeParameterSym
            }
            result
        } ?: emptyMap()

    val typeParameters: Collection<CrTypeParameterSym>
        get() = _typeParameters.values

    fun getTypeParameter(name: String): CrTypeParameterSym? = _typeParameters[name]

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CrMethodSym) return false

        if (namespace != other.namespace) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = namespace.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}
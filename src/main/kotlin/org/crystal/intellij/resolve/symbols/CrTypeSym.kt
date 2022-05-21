package org.crystal.intellij.resolve.symbols

import org.crystal.intellij.psi.CrTypeDefinition
import org.crystal.intellij.psi.CrTypeSource
import org.crystal.intellij.psi.CrVisibility
import org.crystal.intellij.resolve.StableFqName

@Suppress("UnstableApiUsage")
sealed class CrTypeSym(
    name: String,
    sources: List<CrTypeSource>,
) : CrOrdinalSym<CrTypeSource>(name, sources) {
    abstract override val namespace: CrModuleLikeSym

    val visibility: CrVisibility? by lazy {
        val typeDef = sources.firstOrNull { it is CrTypeDefinition } as? CrTypeDefinition
        typeDef?.visibility ?: CrVisibility.PUBLIC
    }

    open val fqName: StableFqName? by lazy {
        StableFqName(name, this.namespace.fqName)
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other::class != this::class) return false
        return fqName == (other as CrTypeSym).fqName
    }

    override fun hashCode() = fqName?.hashCode() ?: 0
}
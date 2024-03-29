package org.crystal.intellij.lang.resolve.symbols

import org.crystal.intellij.lang.psi.CrConstantSource
import org.crystal.intellij.lang.resolve.layout

@Suppress("UnstableApiUsage")
sealed class CrProperTypeSym(
    name: String,
    sources: List<CrConstantSource>,
) : CrTypeSym<CrConstantSource>(name, sources) {
    abstract override val namespace: CrModuleLikeSym

    override val ordinal: CrSymbolOrdinal?
        get() {
            val sources = sources
            if (sources.isEmpty() && fqName?.let { program.layout.getFallbackType(it) } != null) {
                return CrSymbolOrdinal.FALLBACK_ORDINAL
            }
            return super.ordinal
        }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other::class != this::class) return false
        return fqName == (other as CrProperTypeSym).fqName
    }

    override fun hashCode() = fqName?.hashCode() ?: 0
}
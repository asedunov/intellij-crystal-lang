package org.crystal.intellij.resolve.symbols

import org.crystal.intellij.psi.CrMacro
import org.crystal.intellij.resolve.cache.newResolveSlice
import org.crystal.intellij.resolve.cache.resolveCache

@Suppress("UnstableApiUsage")
sealed class CrMacroSym(
    name: String,
    override val namespace: CrModuleLikeSym,
    source: CrMacro
) : CrSym<CrMacro>(name, listOf(source)) {
    class Defined(
        name: String,
        namespace: CrModuleLikeSym,
        source: CrMacro
    ) : CrMacroSym(name, namespace, source) {
        override val origin: Defined
            get() = this
    }

    class Inherited(
        override val origin: Defined,
        override val namespace: CrModuleLikeSym
    ) : CrMacroSym(origin.name, namespace, origin.source)

    companion object {
        private val OVERRIDDEN_MACRO = newResolveSlice<CrMacroSym, CrMacroSym>("OVERRIDDEN_MACRO")
    }

    override val program: CrProgramSym
        get() = namespace.program

    val source: CrMacro
        get() = sources.single()

    abstract val origin: Defined

    val isDefined: Boolean
        get() = this is Defined

    val signature: CrMacroSignature
        get() = source.signature

    val overriddenMacro: CrMacroSym?
        get() = program.project.resolveCache.getOrCompute(OVERRIDDEN_MACRO, this) {
            val relatedMacros = namespace.memberScope.getAllMacros(signature)
            val i = relatedMacros.indexOf(this)
            relatedMacros.getOrNull(i - 1)?.let { return@getOrCompute it }

            null
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this.javaClass != other.javaClass) return false

        return source == (other as CrMacroSym).source
    }

    override fun hashCode() = source.hashCode()
}
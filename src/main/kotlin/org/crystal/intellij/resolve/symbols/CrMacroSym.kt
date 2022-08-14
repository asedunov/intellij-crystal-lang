package org.crystal.intellij.resolve.symbols

import org.crystal.intellij.psi.CrCallArgument
import org.crystal.intellij.psi.CrMacro
import org.crystal.intellij.psi.CrParameterKind
import org.crystal.intellij.resolve.CrCall
import org.crystal.intellij.resolve.cache.newResolveSlice
import org.crystal.intellij.resolve.cache.resolveCache
import java.util.*

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

    val parameters: List<CrMacroParameterSym> by lazy {
        source.parameters.map { CrMacroParameterSym(it, this) }.toList()
    }

    private val splatIndex: Int
        get() = parameters.indexOfFirst { it.source.kind == CrParameterKind.SPLAT }

    private fun matchPositionalArgs(
        args: List<CrCallArgument>,
        action: (param: CrMacroParameterSym, paramIndex: Int, arg: CrCallArgument, argIndex: Int) -> Unit
    ) {
        matchPositionalArgsBeforeSplat(args, action)
        matchPositionalArgsAtSplat(args, action)
    }

    private fun matchPositionalArgsBeforeSplat(
        args: List<CrCallArgument>,
        action: (param: CrMacroParameterSym, paramIndex: Int, arg: CrCallArgument, argIndex: Int) -> Unit
    ) {
        val splatIndex = splatIndex.let { if (it >= 0) it else parameters.size }
        for (i in 0 until splatIndex) {
            val arg = args.getOrNull(i) ?: break
            action(parameters[i], i, arg, i)
        }
    }

    private fun matchPositionalArgsAtSplat(
        args: List<CrCallArgument>,
        action: (param: CrMacroParameterSym, paramIndex: Int, arg: CrCallArgument, argIndex: Int) -> Unit
    ) {
        val splatIndex = splatIndex
        if (splatIndex < 0) return
        val splatSize = args.size - splatIndex
        for (i in 0 until splatSize) {
            val argIndex = splatIndex + i
            val arg = args.getOrNull(argIndex) ?: break
            action(parameters[splatIndex], splatIndex, arg, argIndex)
        }
    }

    fun matches(call: CrCall): Boolean {
        val posArgCount = call.positionalArgs.size
        val paramCount = parameters.size
        var minArgCount = parameters.indexOfFirst { it.hasDefaultValue }
        if (minArgCount < 0) minArgCount = paramCount
        var maxArgCount = paramCount
        val splatIndex = splatIndex

        if (splatIndex >= 0) {
            if (parameters[splatIndex].externalName.isEmpty()) {
                minArgCount = splatIndex
                maxArgCount = splatIndex
            }
            else {
                minArgCount--
                maxArgCount = Int.MAX_VALUE
            }
        }

        if (splatIndex >= 0 &&
            paramCount > splatIndex + 1 &&
            call.namedArgs.isEmpty() &&
            !(splatIndex + 1 until paramCount).all { i -> parameters[i].hasDefaultValue }) return false

        if (posArgCount > maxArgCount) return false

        var mandatoryArgs: BitSet? = null
        if (call.namedArgs.isNotEmpty()) {
            mandatoryArgs = BitSet(paramCount)
        }
        else if (posArgCount < minArgCount) return false

        matchPositionalArgs(call.positionalArgs) { _, paramIndex, _, _ ->
            mandatoryArgs?.set(paramIndex)
        }

        for (namedArg in call.namedArgs) {
            val paramIndex = parameters.indexOfFirst { it.externalName == namedArg.name }
            if (paramIndex < 0) {
                if (signature.hasDoubleSplat) continue else return false
            }
            if (paramIndex == splatIndex) return false
            if (mandatoryArgs != null) {
                if (mandatoryArgs[paramIndex]) return false
                mandatoryArgs.set(paramIndex)
            }
            else {
                if (paramIndex < posArgCount) return false
            }
        }

        if (mandatoryArgs == null) return true
        return parameters.withIndex().all { (i, param) ->
            i == splatIndex || param.hasDefaultValue || mandatoryArgs[i]
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this.javaClass != other.javaClass) return false

        return source == (other as CrMacroSym).source
    }

    override fun hashCode() = source.hashCode()
}
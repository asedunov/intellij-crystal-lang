package org.crystal.intellij.resolve.scopes

import com.intellij.util.SmartFMap
import org.crystal.intellij.psi.CrCallArgument
import org.crystal.intellij.resolve.CrCall
import org.crystal.intellij.resolve.symbols.CrMacroParameterSym
import org.crystal.intellij.resolve.symbols.CrMacroSym
import kotlin.math.min

class CrResolvedMacroCall(
    val call: CrCall,
    val macro: CrMacroSym
) {
    private val parameterMatches: Map<CrMacroParameterSym, CrMacroParameterMatch> by lazy {
        var matches = SmartFMap.emptyMap<CrMacroParameterSym, CrMacroParameterMatch>()
        val parameters = macro.parameters
        val positionalArgs = call.positionalArgs
        val namedArgs = call.namedArgs
        val splatIndex = macro.splatIndex.let { if (it >= 0) it else parameters.size }
        for (i in 0 until min(splatIndex, positionalArgs.size)) {
            val param = parameters[i]
            val arg = positionalArgs.getOrNull(i) ?: break
            matches = matches.plus(param, CrMacroParameterMatch.Simple(arg))
        }
        if (splatIndex < parameters.size && splatIndex < positionalArgs.size) {
            val splatParam = parameters[splatIndex]
            val args = positionalArgs.subList(splatIndex, positionalArgs.size)
            matches = matches.plus(splatParam, CrMacroParameterMatch.Splat(args))
        }
        for (param in parameters) {
            val arg = namedArgs.find { it.name == param.externalName }
            when {
                arg != null -> matches = matches.plus(param, CrMacroParameterMatch.Simple(arg))
                param.hasDefaultValue -> matches = matches.plus(param, CrMacroParameterMatch.DefaultValue)
            }
        }
        matches
    }

    private val argumentMatches: Map<CrCallArgument, CrMacroParameterSym> by lazy {
        var matches = SmartFMap.emptyMap<CrCallArgument, CrMacroParameterSym>()
        val parameters = macro.parameters
        val positionalArgs = call.positionalArgs
        val namedArgs = call.namedArgs
        val splatIndex = macro.splatIndex.let { if (it >= 0) it else parameters.size }
        for (i in 0 until min(splatIndex, positionalArgs.size)) {
            val param = parameters[i]
            val arg = positionalArgs[i]
            matches = matches.plus(arg, param)
        }
        if (splatIndex < parameters.size && splatIndex < positionalArgs.size) {
            val splatParam = parameters[splatIndex]
            for (i in splatIndex until positionalArgs.size) {
                matches = matches.plus(positionalArgs[i], splatParam)
            }
        }
        for (arg in namedArgs) {
            val param = parameters.find { it.externalName == arg.name } ?: continue
            matches = matches.plus(arg, param)
        }
        matches
    }

    fun getParameterMatch(parameter: CrMacroParameterSym) = parameterMatches[parameter]

    fun getArgumentMatch(argument: CrCallArgument) = argumentMatches[argument]
}

sealed class CrMacroParameterMatch {
    object DefaultValue : CrMacroParameterMatch()
    data class Simple(val argument: CrCallArgument) : CrMacroParameterMatch()
    data class Splat(val arguments: List<CrCallArgument>) : CrMacroParameterMatch()
}
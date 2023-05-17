package org.crystal.intellij.parameterInfo

import com.intellij.openapi.util.TextRange
import org.crystal.intellij.presentation.CrystalDefinitionPresentationBase
import org.crystal.intellij.resolve.symbols.CrMacroSym

class CrMacroCallParameterInfo(val macro: CrMacroSym) {
    companion object {
        private const val DELIMITER = ", "
    }

    val parameters: List<String> by lazy {
        macro.parameters.map { CrystalDefinitionPresentationBase.getPresentableText(it.source) }
    }

    val presentableText by lazy {
        if (parameters.isEmpty()) "<no parameters>" else parameters.joinToString(DELIMITER)
    }

    fun getParameterRange(index: Int): TextRange {
        if (index < 0 || index >= parameters.size) return TextRange.EMPTY_RANGE
        val start = parameters.take(index).sumOf { it.length + DELIMITER.length }
        return TextRange(start, start + parameters[index].length)
    }
}
package org.crystal.intellij.ide.search

import com.intellij.openapi.application.QueryExecutorBase
import com.intellij.util.Processor
import org.crystal.intellij.lang.resolve.symbols.CrMacroSym
import org.crystal.intellij.lang.resolve.symbols.CrModuleLikeSym
import org.crystal.intellij.lang.stubs.indexes.CrystalMacroSignatureIndex

class CrystalOverridingMacroSearcher : QueryExecutorBase<CrMacroSym, CrystalOverridingMacroSearch.Parameters>(true) {
    override fun processQuery(
        queryParameters: CrystalOverridingMacroSearch.Parameters,
        consumer: Processor<in CrMacroSym>
    ) {
        val macroSym = queryParameters.macro
        val searchScope = queryParameters.searchScope

        if (processNextMacro(macroSym, macroSym.namespace, consumer)) {
            return
        }

        val candidates = CrystalMacroSignatureIndex[macroSym.signature.serialize(), macroSym.program.project, searchScope]
        for (candidate in candidates) {
            val curMacroSym = candidate.resolveSymbol() ?: continue
            processNextMacro(macroSym, curMacroSym.namespace, consumer)
        }
    }

    private fun processNextMacro(
        macroSym: CrMacroSym,
        namespace: CrModuleLikeSym,
        consumer: Processor<in CrMacroSym>
    ): Boolean {
        val relatedMacros = namespace.memberScope.getAllMacros(macroSym.signature)
        val i = relatedMacros.indexOfFirst {
            it == macroSym || macroSym is CrMacroSym.Defined && it.origin == macroSym
        }
        if (i < 0 || i >= relatedMacros.lastIndex) return false
        val overridingSym = relatedMacros[i + 1]
        if (macroSym.isDefined && !overridingSym.isDefined) return false
        consumer.process(overridingSym)
        return true
    }
}
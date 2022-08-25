package org.crystal.intellij.search

import com.intellij.openapi.application.QueryExecutorBase
import com.intellij.util.Processor
import org.crystal.intellij.resolve.symbols.CrMacroSym
import org.crystal.intellij.resolve.symbols.CrModuleLikeSym
import org.crystal.intellij.stubs.indexes.CrystalMacroSignatureIndex

object CrystalOverridingMacroSearcher : QueryExecutorBase<CrMacroSym, CrystalOverridingMacroSearch.Parameters>(true) {
    override fun processQuery(
        queryParameters: CrystalOverridingMacroSearch.Parameters,
        consumer: Processor<in CrMacroSym>
    ) {
        val macroSym = queryParameters.macro
        val searchScope = queryParameters.searchScope

        if (processNextMacro(macroSym, macroSym.namespace, consumer)) {
            return
        }

        val candidates = CrystalMacroSignatureIndex.get(
            macroSym.signature.serialize(),
            macroSym.program.project,
            searchScope
        )
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
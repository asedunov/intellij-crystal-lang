package org.crystal.intellij.presentation

import com.intellij.model.Symbol
import com.intellij.model.presentation.SymbolPresentation
import com.intellij.model.presentation.SymbolPresentationProvider
import org.crystal.intellij.resolve.symbols.CrSym

@Suppress("UnstableApiUsage")
class CrSymbolPresentationProvider : SymbolPresentationProvider {
    override fun getPresentation(symbol: Symbol): SymbolPresentation? {
        val sym = symbol as? CrSym<*> ?: return null
        return SymbolPresentation.create(sym.getIcon(), sym.name, sym.shortDescription)
    }
}
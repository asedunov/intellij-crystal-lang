package org.crystal.intellij.presentation

import com.intellij.model.Symbol
import com.intellij.model.presentation.SymbolPresentation
import com.intellij.model.presentation.SymbolPresentationProvider
import com.intellij.openapi.util.text.StringUtil
import org.crystal.intellij.resolve.symbols.*

@Suppress("UnstableApiUsage")
class CrSymbolPresentationProvider : SymbolPresentationProvider {
    override fun getPresentation(symbol: Symbol): SymbolPresentation? {
        val sym = symbol as? CrSym<*> ?: return null
        val name = sym.name
        val kind = StringUtil.toTitleCase(sym.presentableKind)
        val icon = sym.getIcon()
        return SymbolPresentation.create(icon, name, "$kind \"$name\"")
    }
}
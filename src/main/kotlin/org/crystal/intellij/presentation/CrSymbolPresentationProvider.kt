package org.crystal.intellij.presentation

import com.intellij.model.Symbol
import com.intellij.model.presentation.SymbolPresentation
import com.intellij.model.presentation.SymbolPresentationProvider
import org.crystal.intellij.resolve.symbols.*

@Suppress("UnstableApiUsage")
class CrSymbolPresentationProvider : SymbolPresentationProvider {
    override fun getPresentation(symbol: Symbol): SymbolPresentation? {
        val sym = symbol as? CrSym<*> ?: return null
        val name = sym.name
        val kind = when (sym) {
            is CrAnnotationSym -> "Annotation"
            is CrClassSym -> "Class"
            is CrEnumSym -> "Enum"
            is CrStructSym -> "Struct"
            is CrCStructSym -> "Struct"
            is CrCUnionSym -> "Union"
            is CrLibrarySym -> "Library"
            is CrModuleSym -> "Module"
            is CrProgramSym -> "Program"
            is CrTypeAliasSym -> "Alias"
            is CrTypeDefSym -> "Type declaration"
            is CrTypeParameterSym -> "Type parameter"
        }
        val icon = sym.getIcon()
        return SymbolPresentation.create(icon, name, "$kind \"$name\"")
    }
}
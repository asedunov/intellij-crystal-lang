package org.crystal.intellij.presentation

import com.intellij.lang.documentation.DocumentationTarget
import com.intellij.lang.documentation.symbol.SymbolDocumentationTargetFactory
import com.intellij.model.Pointer
import com.intellij.model.Symbol
import com.intellij.navigation.TargetPresentation
import com.intellij.openapi.project.Project
import com.intellij.pom.Navigatable
import org.crystal.intellij.resolve.symbols.CrSym

@Suppress("UnstableApiUsage")
class CrSymbolDocumentationTargetFactory : SymbolDocumentationTargetFactory {
    private class DocumentationTargetImpl(
        private val symbol: CrSym<*>
    ) : DocumentationTarget {
        override val presentation: TargetPresentation
            get() = TargetPresentation.builder(symbol.shortDescription).icon(symbol.getIcon()).presentation()

        override val navigatable: Navigatable?
            get() = symbol.sources.firstOrNull()

        override fun computeDocumentationHint() = symbol.shortDescription

        override fun createPointer(): Pointer<out DocumentationTarget> {
            val symbolPointer = symbol.createPointer()
            return object : Pointer<DocumentationTargetImpl> {
                override fun dereference(): DocumentationTargetImpl? {
                    val symbol = symbolPointer.dereference() ?: return null
                    return DocumentationTargetImpl(symbol)
                }
            }
        }
    }

    override fun documentationTarget(project: Project, symbol: Symbol): DocumentationTarget? {
        if (symbol !is CrSym<*>) return null
        return DocumentationTargetImpl(symbol)
    }
}
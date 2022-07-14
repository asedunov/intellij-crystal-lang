package org.crystal.intellij.search

import com.intellij.model.Symbol
import com.intellij.model.psi.PsiSymbolReference
import com.intellij.model.search.CodeReferenceSearcher
import com.intellij.model.search.LeafOccurrence
import com.intellij.model.search.SearchRequest
import com.intellij.openapi.project.Project
import org.crystal.intellij.CrystalLanguage
import org.crystal.intellij.psi.CrPathNameElement
import org.crystal.intellij.resolve.symbols.CrSym

@Suppress("UnstableApiUsage")
class CrystalCodeReferenceSearcher : CodeReferenceSearcher {
    override fun getReferencingLanguage(target: Symbol) = CrystalLanguage

    override fun getSearchRequest(project: Project, target: Symbol): SearchRequest? {
        return if (target is CrSym<*>) SearchRequest.of(target.name) else null
    }

    override fun getReferences(target: Symbol, occurrence: LeafOccurrence): Collection<PsiSymbolReference> {
        val path = occurrence.start.parent as? CrPathNameElement ?: return emptyList()
        if (path.resolveSymbol() != target) return emptyList()
        return path.ownReferences
    }
}
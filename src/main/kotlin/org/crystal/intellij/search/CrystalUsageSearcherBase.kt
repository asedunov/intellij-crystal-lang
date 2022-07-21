package org.crystal.intellij.search

import com.intellij.model.search.*
import com.intellij.psi.search.SearchScope
import com.intellij.psi.util.parentOfType
import com.intellij.refactoring.util.TextOccurrencesUtilBase
import com.intellij.util.Query
import com.intellij.util.codeInsight.CommentUtilCore
import org.crystal.intellij.CrystalLanguage
import org.crystal.intellij.psi.CrPathNameElement
import org.crystal.intellij.references.CrPathReference

@Suppress("UnstableApiUsage")
abstract class CrystalUsageSearcherBase<U, P : SearchParameters<U>> {
    protected fun buildQuery(parameters: P): Query<out U>? {
        val target = getTarget(parameters) ?: return null
        val project = parameters.project
        val searchScope = getSearchScope(parameters)
        val targetPointer = target.createPointer()
        return SearchService
            .getInstance()
            .searchWord(project, target.targetName)
            .inScope(searchScope)
            .inFilesWithLanguage(CrystalLanguage)
            .inContexts(SearchContext.IN_CODE)
            .buildQuery(LeafOccurrenceMapper.withPointer(targetPointer, ::toUsages))
    }

    protected abstract fun getTarget(parameters: P): CrystalConstantLikeSearchRenameTarget?

    protected abstract fun getSearchScope(parameters: P): SearchScope

    protected abstract fun toUsage(ref: CrPathReference): U

    private fun toUsages(
        target: CrystalConstantLikeSearchRenameTarget,
        occurrence: LeafOccurrence
    ): List<U> {
        val element = occurrence.start
        if (CommentUtilCore.isComment(element) || TextOccurrencesUtilBase.isStringLiteralElement(element)) {
            return emptyList()
        }

        val path = element.parentOfType<CrPathNameElement>() ?: return emptyList()
        val symbol = target.symbol
        val ref = path.ownReference
        if (!ref.resolvesTo(symbol)) return emptyList()

        return listOf(toUsage(ref))
    }
}
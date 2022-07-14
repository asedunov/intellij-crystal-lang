package org.crystal.intellij.findUsages

import com.intellij.find.usages.api.PsiUsage
import com.intellij.find.usages.api.Usage
import com.intellij.find.usages.api.UsageSearchParameters
import com.intellij.find.usages.api.UsageSearcher
import com.intellij.model.search.LeafOccurrence
import com.intellij.model.search.LeafOccurrenceMapper
import com.intellij.model.search.SearchContext
import com.intellij.model.search.SearchService
import com.intellij.psi.util.parentOfType
import com.intellij.refactoring.util.TextOccurrencesUtilBase
import com.intellij.usages.impl.rules.UsageType
import com.intellij.util.Query
import com.intellij.util.codeInsight.CommentUtilCore
import org.crystal.intellij.CrystalLanguage
import org.crystal.intellij.psi.CrPathNameElement
import org.crystal.intellij.psi.CrType
import org.crystal.intellij.psi.parents
import org.crystal.intellij.search.CrystalConstantLikeSearchTarget

@Suppress("UnstableApiUsage")
class CrystalUsageSearcher : UsageSearcher {
    companion object {
        private val TYPE_REFERENCE = UsageType { "Type reference" }
        private val CONSTANT_REFERENCE = UsageType { "Constant reference" }
    }

    override fun collectSearchRequest(parameters: UsageSearchParameters): Query<out Usage>? {
        val target = parameters.target as? CrystalConstantLikeSearchTarget ?: return null
        val project = parameters.project
        val searchScope = parameters.searchScope
        val targetPointer = target.createPointer()
        return SearchService
            .getInstance()
            .searchWord(project, target.targetName)
            .inScope(searchScope)
            .inFilesWithLanguage(CrystalLanguage)
            .inContexts(SearchContext.IN_CODE)
            .buildQuery(LeafOccurrenceMapper.withPointer(targetPointer, ::toUsages))
    }

    private fun toUsages(
        target: CrystalConstantLikeSearchTarget,
        occurrence: LeafOccurrence
    ): List<PsiUsage> {
        val element = occurrence.start
        if (CommentUtilCore.isComment(element) || TextOccurrencesUtilBase.isStringLiteralElement(element)) {
            return emptyList()
        }
        val path = element.parentOfType<CrPathNameElement>() ?: return emptyList()
        val symbol = target.symbol
        return path.ownReferences.mapNotNull { ref ->
            if (!ref.resolvesTo(symbol)) return@mapNotNull null
            val usageType = if (path.parents().any { it is CrType<*> }) TYPE_REFERENCE else CONSTANT_REFERENCE
            DelegatingPsiUsage(PsiUsage.Companion.textUsage(ref), usageType)
        }
    }
}
package org.crystal.intellij.findUsages

import com.intellij.find.usages.api.PsiUsage
import com.intellij.find.usages.api.Usage
import com.intellij.find.usages.api.UsageSearchParameters
import com.intellij.find.usages.api.UsageSearcher
import com.intellij.psi.search.SearchScope
import com.intellij.usages.impl.rules.UsageType
import com.intellij.util.Query
import org.crystal.intellij.psi.*
import org.crystal.intellij.references.CrPathReference
import org.crystal.intellij.search.CrystalConstantLikeSearchRenameTarget
import org.crystal.intellij.search.CrystalUsageSearcherBase

@Suppress("UnstableApiUsage")
class CrystalUsageSearcher: CrystalUsageSearcherBase<Usage, UsageSearchParameters>(), UsageSearcher {
    companion object {
        private val TYPE_REFERENCE = UsageType { "Type reference" }
        private val CONSTANT_REFERENCE = UsageType { "Constant reference" }
    }

    override fun collectSearchRequest(parameters: UsageSearchParameters): Query<out Usage>? {
        return buildQuery(parameters)
    }

    override fun getTarget(parameters: UsageSearchParameters): CrystalConstantLikeSearchRenameTarget? {
        return parameters.target as? CrystalConstantLikeSearchRenameTarget
    }

    override fun getSearchScope(parameters: UsageSearchParameters): SearchScope {
        return parameters.searchScope
    }

    override fun toUsage(ref: CrPathReference): Usage? {
        val path = ref.element
        if (path.parentStubsOrPsi().skipWhile { it is CrPathNameElement }.first() is CrDefinition) return null
        val usageType = if (path.parents().any { it is CrType<*> }) TYPE_REFERENCE else CONSTANT_REFERENCE
        return DelegatingPsiUsage(PsiUsage.Companion.textUsage(ref), usageType)
    }
}
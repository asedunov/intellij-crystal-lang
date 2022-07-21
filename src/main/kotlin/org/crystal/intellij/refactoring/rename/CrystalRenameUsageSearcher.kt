package org.crystal.intellij.refactoring.rename

import com.intellij.find.usages.api.PsiUsage
import com.intellij.psi.search.SearchScope
import com.intellij.refactoring.rename.api.PsiModifiableRenameUsage
import com.intellij.refactoring.rename.api.RenameUsage
import com.intellij.refactoring.rename.api.RenameUsageSearchParameters
import com.intellij.refactoring.rename.api.RenameUsageSearcher
import com.intellij.util.Query
import org.crystal.intellij.references.CrPathReference
import org.crystal.intellij.search.CrystalConstantLikeSearchRenameTarget
import org.crystal.intellij.search.CrystalUsageSearcherBase

@Suppress("UnstableApiUsage")
class CrystalRenameUsageSearcher : CrystalUsageSearcherBase<RenameUsage, RenameUsageSearchParameters>(), RenameUsageSearcher {
    override fun collectSearchRequest(parameters: RenameUsageSearchParameters): Query<out RenameUsage>? {
        return buildQuery(parameters)
    }

    override fun getTarget(parameters: RenameUsageSearchParameters): CrystalConstantLikeSearchRenameTarget? {
        return parameters.target as? CrystalConstantLikeSearchRenameTarget
    }

    override fun getSearchScope(parameters: RenameUsageSearchParameters): SearchScope {
        return parameters.searchScope
    }

    override fun toUsage(ref: CrPathReference): RenameUsage {
        return PsiModifiableRenameUsage.defaultPsiModifiableRenameUsage(PsiUsage.Companion.textUsage(ref))
    }
}
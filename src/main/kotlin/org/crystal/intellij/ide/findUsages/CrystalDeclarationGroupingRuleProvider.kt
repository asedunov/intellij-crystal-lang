package org.crystal.intellij.ide.findUsages

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.intellij.usages.*
import com.intellij.usages.impl.FileStructureGroupRuleProvider
import com.intellij.usages.rules.SingleParentUsageGroupingRule
import org.crystal.intellij.lang.psi.CrDefinition
import org.crystal.intellij.lang.psi.CrFunctionLikeDefinition
import org.crystal.intellij.lang.psi.CrTypeDefinition

sealed class CrystalDeclarationGroupingRuleProvider : FileStructureGroupRuleProvider {
    @Suppress("CanSealedSubClassBeObject")
    class ByType : CrystalDeclarationGroupingRuleProvider() {
        override fun getDefinition(usageElement: PsiElement?) = usageElement?.parentOfType<CrTypeDefinition>()
    }

    @Suppress("CanSealedSubClassBeObject")
    class ByFunction : CrystalDeclarationGroupingRuleProvider() {
        override fun getDefinition(usageElement: PsiElement?) = usageElement?.parentOfType<CrFunctionLikeDefinition>()
    }

    protected abstract fun getDefinition(usageElement: PsiElement?): CrDefinition?

    override fun getUsageGroupingRule(project: Project) = object : SingleParentUsageGroupingRule() {
        override fun getParentGroupFor(usage: Usage, targets: Array<out UsageTarget>): UsageGroup? {
            if (usage !is UsageInfo2UsageAdapter) return null
            val element = usage.element ?: return null
            val range = usage.usageInfo.rangeInElement ?: return null
            val type = getDefinition(element.findElementAt(range.startOffset)) ?: return null
            return PsiElementUsageGroupBase<CrDefinition>(type)
        }
    }
}
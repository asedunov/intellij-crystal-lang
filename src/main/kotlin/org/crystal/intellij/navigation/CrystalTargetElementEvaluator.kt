package org.crystal.intellij.navigation

import com.intellij.codeInsight.TargetElementEvaluatorEx2
import com.intellij.psi.PsiElement
import org.crystal.intellij.psi.*

class CrystalTargetElementEvaluator : TargetElementEvaluatorEx2() {
    override fun getNamedElement(element: PsiElement): PsiElement? {
        val named = element.parentStubOrPsiOfType<CrNamedElement>() ?: return null
        val nameElement = named.nameElement ?: return null
        val isId = when (nameElement) {
            is CrSimpleNameElement -> true
            is CrPathNameElement -> nameElement.textRange.endOffset == element.textRange.endOffset
        }
        return if (isId) nameElement.parentStubOrPsi() as? CrNameElementHolder else null
    }

    override fun isAcceptableNamedParent(parent: PsiElement) = false
}
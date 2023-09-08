package org.crystal.intellij.ide.refactoring.rename

import com.intellij.openapi.util.Condition
import com.intellij.psi.PsiElement
import org.crystal.intellij.lang.psi.CrConstantSource

class CrystalVetoRenameCondition : Condition<PsiElement> {
    override fun value(element: PsiElement) = element is CrConstantSource
}
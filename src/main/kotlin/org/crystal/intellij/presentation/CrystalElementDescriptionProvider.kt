package org.crystal.intellij.presentation

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.ElementDescriptionProvider
import com.intellij.psi.PsiElement
import com.intellij.usageView.UsageViewTypeLocation
import org.crystal.intellij.psi.CrNamedElement
import org.crystal.intellij.psi.presentableKind

class CrystalElementDescriptionProvider : ElementDescriptionProvider {
    override fun getElementDescription(element: PsiElement, location: ElementDescriptionLocation): String? {
        if (location == UsageViewTypeLocation.INSTANCE && element is CrNamedElement) {
            return element.presentableKind
        }
        return null
    }
}
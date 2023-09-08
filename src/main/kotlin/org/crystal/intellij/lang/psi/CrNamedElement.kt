package org.crystal.intellij.lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner

sealed interface CrNamedElement : CrNameElementHolder, PsiNameIdentifierOwner {
    override fun getNameIdentifier() = nameElement

    override fun setName(name: String): PsiElement {
        nameElement?.setName(name)
        return this
    }
}
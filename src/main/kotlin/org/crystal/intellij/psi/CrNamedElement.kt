package org.crystal.intellij.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner

sealed interface CrNamedElement : CrNameElementHolder, PsiNameIdentifierOwner {
    override fun getNameIdentifier() = nameElement

    override fun setName(name: String): PsiElement {
        throw UnsupportedOperationException()
    }
}
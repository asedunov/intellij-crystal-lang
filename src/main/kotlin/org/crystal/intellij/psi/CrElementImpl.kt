package org.crystal.intellij.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElementVisitor

abstract class CrElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), CrElement {
    override fun accept(visitor: PsiElementVisitor) {
        if (visitor is CrVisitor) accept(visitor) else super<ASTWrapperPsiElement>.accept(visitor)
    }

    override fun getName(): String? {
        if (this is CrNameElementHolder) return nameElement?.name
        return super.getName()
    }
}
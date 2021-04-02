package org.crystal.intellij.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElementVisitor

abstract class CrElement(node: ASTNode) : ASTWrapperPsiElement(node) {
    open fun accept(visitor: CrVisitor) = visitor.visitCrElement(this)

    override fun accept(visitor: PsiElementVisitor) {
        if (visitor is CrVisitor) accept(visitor) else super.accept(visitor)
    }
}
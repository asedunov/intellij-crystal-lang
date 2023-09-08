package org.crystal.intellij.lang.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElementVisitor

abstract class CrElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), CrElement {
    override fun accept(visitor: PsiElementVisitor) = acceptElement(visitor)

    override fun getName() = elementName

    override fun getTextOffset() = elementOffset
}
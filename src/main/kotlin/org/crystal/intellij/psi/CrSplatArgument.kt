package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement

class CrSplatArgument(node: ASTNode) : CrElementImpl(node), CrCallArgument {
    override fun accept(visitor: CrVisitor) = visitor.visitSplatArgument(this)

    val splatElement: PsiElement
        get() = firstChild
}
package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.crystal.intellij.lang.lexer.CR_RESCUE

class CrRescueExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitRescueExpression(this)

    val keyword: PsiElement
        get() = findChildByType(CR_RESCUE)!!

    val argument: CrExpression
        get() = childOfType()!!

    val body: CrExpression?
        get() = argument.nextSiblingOfType()
}
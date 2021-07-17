package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.elementType

class CrPseudoConstantExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitPseudoConstantExpression(this)

    val tokenType: IElementType?
        get() = firstChild?.elementType
}
package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable

class CrArrayLiteralExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitArrayLiteralExpression(this)

    val expressions: JBIterable<CrExpression>
        get() = childrenOfType()

    val type: CrTypeElement<*>?
        get() = childOfType()
}
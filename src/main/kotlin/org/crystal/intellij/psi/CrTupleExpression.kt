package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable

class CrTupleExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitTupleExpression(this)

    val expressions: JBIterable<CrExpression>
        get() = childrenOfType()
}
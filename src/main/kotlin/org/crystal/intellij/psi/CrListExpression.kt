package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable

class CrListExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitListExpression(this)

    val expressions: JBIterable<CrExpression>
        get() = childrenOfType()
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable

class CrSelectExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitSelectExpression(this)

    val whenClauses: JBIterable<CrWhenClause>
        get() = childrenOfType()
}
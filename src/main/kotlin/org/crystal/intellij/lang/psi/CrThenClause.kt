package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable

class CrThenClause(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitThenClause(this)

    val expressions: JBIterable<CrExpression>
        get() = childrenOfType()
}
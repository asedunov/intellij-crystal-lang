package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable

class CrHashExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitHashExpression(this)

    val entries: JBIterable<CrHashEntry>
        get() = childrenOfType()

    val type: CrType?
        get() = childOfType()
}
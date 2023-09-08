package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable

class CrStringArrayExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitStringArrayExpression(this)

    val elements: JBIterable<CrStringLiteralExpression>
        get() = childrenOfType()
}
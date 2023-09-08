package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable

class CrSymbolArrayExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitSymbolArrayExpression(this)

    val elements: JBIterable<CrSymbolExpression>
        get() = childrenOfType()
}
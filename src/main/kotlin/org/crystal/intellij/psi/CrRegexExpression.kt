package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrRegexExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitRegexExpression(this)
}
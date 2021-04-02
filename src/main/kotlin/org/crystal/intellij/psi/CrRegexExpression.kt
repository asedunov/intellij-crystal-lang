package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrRegexExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitRegexExpression(this)
}
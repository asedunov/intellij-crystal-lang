package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrIncludeExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitIncludeExpression(this)
}
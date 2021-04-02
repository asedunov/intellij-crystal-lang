package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrIsNilExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitIsNilExpression(this)
}
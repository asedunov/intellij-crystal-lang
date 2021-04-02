package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrUnlessExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitUnlessExpression(this)
}
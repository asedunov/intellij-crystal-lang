package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrUnlessExpression(node: ASTNode) : CrIfUnlessExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitUnlessExpression(this)
}
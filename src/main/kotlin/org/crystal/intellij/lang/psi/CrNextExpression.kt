package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrNextExpression(node: ASTNode) : CrVoidExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitNextExpression(this)
}
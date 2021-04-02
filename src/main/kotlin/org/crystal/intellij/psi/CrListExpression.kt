package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrListExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitListExpression(this)
}
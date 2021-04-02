package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrWhileExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitWhileExpression(this)
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrWhileExpression(node: ASTNode) : CrLoopExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitWhileExpression(this)
}
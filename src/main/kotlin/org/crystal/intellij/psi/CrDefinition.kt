package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

abstract class CrDefinition(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitDefinition(this)
}
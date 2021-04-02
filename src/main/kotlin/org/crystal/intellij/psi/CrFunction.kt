package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrFunction(node: ASTNode) : CrDefinition(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitFunction(this)
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrVariable(node: ASTNode) : CrDefinition(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitVariable(this)
}
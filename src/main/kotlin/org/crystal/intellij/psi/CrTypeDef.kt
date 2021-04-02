package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrTypeDef(node: ASTNode) : CrDefinition(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitTypeDef(this)
}
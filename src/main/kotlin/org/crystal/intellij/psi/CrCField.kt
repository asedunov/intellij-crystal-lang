package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrCField(node: ASTNode) : CrDefinition(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitCField(this)
}
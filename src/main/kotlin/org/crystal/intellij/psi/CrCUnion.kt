package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrCUnion(node: ASTNode) : CrDefinition(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitCUnion(this)
}
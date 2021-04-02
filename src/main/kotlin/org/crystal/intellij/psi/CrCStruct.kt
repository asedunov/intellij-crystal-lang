package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrCStruct(node: ASTNode) : CrDefinition(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitCStruct(this)
}
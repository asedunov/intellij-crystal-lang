package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrEnum(node: ASTNode) : CrDefinition(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitEnum(this)
}
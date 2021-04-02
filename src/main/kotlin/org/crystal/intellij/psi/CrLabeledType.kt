package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrLabeledType(node: ASTNode) : CrType(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitLabeledType(this)
}
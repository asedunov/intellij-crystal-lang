package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrMultiParameter(node: ASTNode) : CrParameter(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitMultiParameter(this)
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrHeredocLiteralBody(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitHeredocLiteralBody(this)
}
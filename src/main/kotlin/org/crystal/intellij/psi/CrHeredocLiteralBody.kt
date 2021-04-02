package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrHeredocLiteralBody(node: ASTNode) : CrElement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitHeredocLiteralBody(this)
}
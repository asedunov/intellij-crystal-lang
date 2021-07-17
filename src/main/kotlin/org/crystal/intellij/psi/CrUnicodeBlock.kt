package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrUnicodeBlock(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitUnicodeBlock(this)
}
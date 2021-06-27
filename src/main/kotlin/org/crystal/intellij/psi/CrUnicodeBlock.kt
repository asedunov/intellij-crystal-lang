package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrUnicodeBlock(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitUnicodeBlock(this)
}
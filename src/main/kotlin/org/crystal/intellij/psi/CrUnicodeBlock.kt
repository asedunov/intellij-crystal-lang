package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrUnicodeBlock(node: ASTNode) : CrElement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitUnicodeBlock(this)
}
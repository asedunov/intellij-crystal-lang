package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrTypeBody(node: ASTNode) : CrElement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitTypeBody(this)
}
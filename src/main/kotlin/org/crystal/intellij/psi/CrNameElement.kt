package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrNameElement(node: ASTNode) : CrElement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitNameElement(this)
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrNamedTupleEntry(node: ASTNode) : CrElement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitNamedTupleEntry(this)
}
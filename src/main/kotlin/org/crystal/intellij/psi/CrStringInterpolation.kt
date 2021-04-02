package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrStringInterpolation(node: ASTNode) : CrElement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitStringInterpolation(this)
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrExceptionHandler(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitExceptionHandler(this)
}
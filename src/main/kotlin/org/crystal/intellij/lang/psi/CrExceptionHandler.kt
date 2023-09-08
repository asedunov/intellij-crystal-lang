package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrExceptionHandler(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitExceptionHandler(this)
}
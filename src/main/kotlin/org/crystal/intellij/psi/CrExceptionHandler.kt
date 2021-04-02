package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrExceptionHandler(node: ASTNode) : CrElement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitExceptionHandler(this)
}
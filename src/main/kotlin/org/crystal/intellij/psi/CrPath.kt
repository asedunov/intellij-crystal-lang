package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrPath(node: ASTNode) : CrElement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitPath(this)
}
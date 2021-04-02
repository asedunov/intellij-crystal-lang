package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrCFieldGroup(node: ASTNode) : CrElement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitCFieldGroup(this)
}
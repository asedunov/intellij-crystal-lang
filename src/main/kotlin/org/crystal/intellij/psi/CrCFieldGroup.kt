package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrCFieldGroup(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitCFieldGroup(this)

    val type: CrType?
        get() = childOfType()
}
package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrOutArgument(node: ASTNode) : CrElementImpl(node), CrCallArgument {
    override fun accept(visitor: CrVisitor) = visitor.visitOutArgument(this)

    val expression: CrReferenceExpression?
        get() = childOfType()
}
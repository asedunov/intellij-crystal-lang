package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrNamedArgument(node: ASTNode) : CrElementImpl(node), CrNamedElement, CrSimpleNameElementHolder, CrCallArgument {
    override fun accept(visitor: CrVisitor) = visitor.visitNamedArgument(this)

    val argument: CrCallArgument?
        get() = childOfType()
}
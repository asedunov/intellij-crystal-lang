package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrOutArgument(node: ASTNode) : CrElementImpl(node), CrCallArgument {
    override fun accept(visitor: CrVisitor) = visitor.visitOutArgument(this)
}
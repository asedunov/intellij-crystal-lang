package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrSplatArgument(node: ASTNode) : CrElementImpl(node), CrCallArgument {
    override fun accept(visitor: CrVisitor) = visitor.visitSplatArgument(this)
}
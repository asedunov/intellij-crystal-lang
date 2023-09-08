package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrShortBlockArgument(node: ASTNode) : CrElementImpl(node), CrCallArgument {
    override fun accept(visitor: CrVisitor) = visitor.visitShortBlockArgument(this)
}
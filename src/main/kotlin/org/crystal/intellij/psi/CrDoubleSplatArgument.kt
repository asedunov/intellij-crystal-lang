package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrDoubleSplatArgument(node: ASTNode) : CrElementImpl(node), CrCallArgument {
    override fun accept(visitor: CrVisitor) = visitor.visitDoubleSplatArgument(this)
}
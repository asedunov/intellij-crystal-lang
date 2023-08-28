package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrMacroWrapperStatement(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitMacroWrapperStatement(this)
}
package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrMacroUnlessStatement(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitMacroUnlessStatement(this)
}
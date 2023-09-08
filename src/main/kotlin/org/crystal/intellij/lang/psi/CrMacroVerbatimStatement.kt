package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrMacroVerbatimStatement(node: ASTNode) : CrExpressionImpl(node), CrMacroLiteralElement {
    override fun accept(visitor: CrVisitor) = visitor.visitMacroVerbatimStatement(this)

    val literal: CrMacroLiteral?
        get() = childOfType()
}
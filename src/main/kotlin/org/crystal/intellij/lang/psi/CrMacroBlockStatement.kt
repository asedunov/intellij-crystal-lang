package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrMacroBlockStatement(node: ASTNode) : CrExpressionImpl(node), CrMacroLiteralElement {
    override fun accept(visitor: CrVisitor) = visitor.visitMacroBlockStatement(this)

    val body: CrMacroLiteral?
        get() = childOfType()
}
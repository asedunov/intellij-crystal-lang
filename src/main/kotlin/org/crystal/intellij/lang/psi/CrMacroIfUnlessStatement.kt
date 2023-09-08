package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

sealed class CrMacroIfUnlessStatement(node: ASTNode) : CrExpressionImpl(node), CrMacroLiteralElement {
    val condition: CrExpression?
        get() = childOfType()

    val thenClause: CrMacroLiteral?
        get() = childOfType()

    val elseClause: CrMacroLiteral?
        get() = thenClause?.nextSiblingOfType()
}
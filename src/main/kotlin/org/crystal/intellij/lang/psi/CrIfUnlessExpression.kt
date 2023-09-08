package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

sealed class CrIfUnlessExpression(node: ASTNode) : CrExpressionImpl(node) {
    val isSuffix: Boolean
        get() = firstChild is CrThenClause

    val condition: CrExpression?
        get() = childOfType()

    val thenClause: CrThenClause?
        get() = childOfType()

    val elseClause: CrElseClause?
        get() = childOfType()
}
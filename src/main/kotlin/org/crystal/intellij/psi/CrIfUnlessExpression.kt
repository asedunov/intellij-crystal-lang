package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

abstract class CrIfUnlessExpression(node: ASTNode) : CrExpressionImpl(node) {
    val isSuffix: Boolean
        get() = firstChild is CrThenClause
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

sealed class CrLoopExpression(node: ASTNode) : CrExpressionImpl(node) {
    val condition: CrExpression?
        get() = childOfType()

    val body: CrBlockExpression?
        get() = condition?.nextSiblingOfType()
}
package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstVisitor

class CstOut(
    expression: CstNode,
    location: CstLocation? = null
) : CstUnaryExpression(expression, location) {
    override fun acceptSelf(visitor: CstVisitor) = visitor.visitOut(this)
}
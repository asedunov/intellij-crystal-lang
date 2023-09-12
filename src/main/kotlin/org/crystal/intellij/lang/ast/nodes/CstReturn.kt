package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstVisitor

class CstReturn(
    expression: CstNode? = null,
    location: CstLocation? = null
) : CstControlExpression(expression, location) {
    override fun acceptSelf(visitor: CstVisitor) = visitor.visitReturn(this)
}
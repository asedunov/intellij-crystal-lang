package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstAnd(
    left: CstNode,
    right: CstNode,
    location: CstLocation? = null
) : CstBinaryOp(left, right, location) {
    override fun acceptSelf(visitor: CstVisitor) = visitor.visitAnd(this)
}
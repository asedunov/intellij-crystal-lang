package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstVisitor

class CstOr(
    left: CstNode,
    right: CstNode,
    location: CstLocation? = null
) : CstBinaryOp(left, right, location) {
    override fun acceptSelf(visitor: CstVisitor) = visitor.visitOr(this)
}
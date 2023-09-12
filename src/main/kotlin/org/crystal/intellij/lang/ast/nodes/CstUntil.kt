package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstUntil(
    condition: CstNode,
    body: CstNode = CstNop,
    location: CstLocation? = null
) : CstLoopBase(condition, body, location) {
    override fun acceptSelf(visitor: CstVisitor) = visitor.visitUntil(this)
}
package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstIf(
    condition: CstNode,
    thenBranch: CstNode = CstNop,
    elseBranch: CstNode = CstNop,
    val isTernary: Boolean = false,
    location: CstLocation? = null
) : CstConditionalExpression(condition, thenBranch, elseBranch, location) {
    override fun acceptSelf(visitor: CstVisitor) = visitor.visitIf(this)
}
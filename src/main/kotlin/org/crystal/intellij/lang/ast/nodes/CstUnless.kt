package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstUnless(
    condition: CstNode,
    thenBranch: CstNode = CstNop,
    elseBranch: CstNode = CstNop,
    location: CstLocation? = null
) : CstConditionalExpression(condition, thenBranch, elseBranch, location) {
    override fun acceptSelf(visitor: CstVisitor) = visitor.visitUnless(this)
}
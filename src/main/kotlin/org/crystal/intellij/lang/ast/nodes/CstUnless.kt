package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstUnless(
    condition: CstNode<*>,
    thenBranch: CstNode<*> = CstNop,
    elseBranch: CstNode<*> = CstNop,
    location: CstLocation? = null
) : CstConditionalExpression<CstUnless>(condition, thenBranch, elseBranch, location) {
    fun copy(
        condition: CstNode<*> = this.condition,
        thenBranch: CstNode<*> = this.thenBranch,
        elseBranch: CstNode<*> = this.elseBranch,
        location: CstLocation? = this.location
    ) = CstUnless(condition, thenBranch, elseBranch, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitUnless(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformUnless(this)
}
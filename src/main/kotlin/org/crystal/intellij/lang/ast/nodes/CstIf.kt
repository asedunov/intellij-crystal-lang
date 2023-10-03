package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstIf(
    condition: CstNode<*>,
    thenBranch: CstNode<*> = CstNop,
    elseBranch: CstNode<*> = CstNop,
    val isTernary: Boolean = false,
    location: CstLocation? = null
) : CstConditionalExpression<CstIf>(condition, thenBranch, elseBranch, location) {
    fun copy(
        condition: CstNode<*> = this.condition,
        thenBranch: CstNode<*> = this.thenBranch,
        elseBranch: CstNode<*> = this.elseBranch,
        isTernary: Boolean = this.isTernary,
        location: CstLocation? = this.location
    ) = CstIf(condition, thenBranch, elseBranch, isTernary, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitIf(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformIf(this)
}
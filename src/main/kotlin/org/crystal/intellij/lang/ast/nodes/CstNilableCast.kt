package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstNilableCast(
    obj: CstNode<*>,
    type: CstNode<*>,
    location: CstLocation? = null
) : CstCastBase<CstNilableCast>(obj, type, location) {
    override fun copy(
        obj: CstNode<*>,
        type: CstNode<*>,
        location: CstLocation?
    ) = CstNilableCast(obj, type, location)

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitNilableCast(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformNilableCast(this)
}
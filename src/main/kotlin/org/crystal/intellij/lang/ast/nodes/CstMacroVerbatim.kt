package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstMacroVerbatim(
    expression: CstNode<*>,
    location: CstLocation? = null
) : CstUnaryExpression<CstMacroVerbatim>(expression, location) {
    override fun copy(
        expression: CstNode<*>,
        location: CstLocation?
    ) = CstMacroVerbatim(expression, location)
    
    override fun acceptSelf(visitor: CstVisitor) = visitor.visitMacroVerbatim(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformMacroVerbatim(this)
}
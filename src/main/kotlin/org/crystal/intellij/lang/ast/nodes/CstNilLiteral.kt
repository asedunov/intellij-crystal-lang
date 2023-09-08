package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstNilLiteral(
    location: CstLocation? = null,
) : CstNode(location), CstSimpleLiteral {
    override fun equals(other: Any?) = this === other || javaClass == other?.javaClass

    override fun hashCode() = javaClass.hashCode()

    override fun toString() = "NilLiteral"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitNilLiteral(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformNilLiteral(this)
}
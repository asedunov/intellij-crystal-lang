package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstSelf(
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?) = this === other || javaClass == other?.javaClass

    override fun hashCode() = javaClass.hashCode()

    override fun toString() = "Self"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitSelf(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformSelf(this)
}
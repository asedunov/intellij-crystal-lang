package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstUnderscore(
    location: CstLocation? = null,
) : CstNode<CstUnderscore>(location) {
    companion object {
        val EMPTY = CstUnderscore()
    }

    override fun withLocation(location: CstLocation?) = CstUnderscore(location)

    override fun equals(other: Any?) = this === other || javaClass == other?.javaClass

    override fun hashCode() = javaClass.hashCode()

    override fun toString() = "Underscore"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitUnderscore(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformUnderscore(this)
}
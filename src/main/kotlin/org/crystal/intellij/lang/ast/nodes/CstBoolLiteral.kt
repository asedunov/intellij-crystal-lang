package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

sealed class CstBoolLiteral(
    val value: Boolean,
    location: CstLocation? = null
) : CstNode(location) {
    class False(location: CstLocation? = null) : CstBoolLiteral(false, location)
    class True(location: CstLocation? = null) : CstBoolLiteral(true, location)

    companion object {
        fun of(
            value: Boolean,
            location: CstLocation? = null
        ) = if (value) True(location) else False(location)
    }

    override fun equals(other: Any?): Boolean {
        return this === other ||
                other is CstBoolLiteral && value == other.value
    }

    override fun hashCode() = value.hashCode()

    override fun toString() = "BoolLiteral($value)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitBoolLiteral(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformBoolLiteral(this)
}
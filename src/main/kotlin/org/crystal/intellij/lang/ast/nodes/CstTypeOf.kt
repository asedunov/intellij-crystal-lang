package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstTypeOf(
    val expressions: List<CstNode<*>>,
    location: CstLocation? = null
) : CstNode<CstTypeOf>(location) {
    fun copy(
        expressions: List<CstNode<*>> = this.expressions,
        location: CstLocation? = this.location
    ) = CstTypeOf(expressions, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstTypeOf

        return expressions == other.expressions
    }

    override fun hashCode() = expressions.hashCode()

    override fun toString() = "TypeOf($expressions)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitTypeOf(this)

    override fun acceptChildren(visitor: CstVisitor) {
        expressions.forEach { it.accept(visitor) }
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformTypeOf(this)
}
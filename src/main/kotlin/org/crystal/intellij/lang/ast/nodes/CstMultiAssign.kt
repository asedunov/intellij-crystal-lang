package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstMultiAssign(
    val targets: List<CstNode<*>>,
    val values: List<CstNode<*>>,
    location: CstLocation? = null
) : CstNode<CstMultiAssign>(location) {
    fun copy(
        targets: List<CstNode<*>> = this.targets,
        values: List<CstNode<*>> = this.values,
        location: CstLocation? = this.location
    ) = CstMultiAssign(targets, values, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstMultiAssign

        if (targets != other.targets) return false
        if (values != other.values) return false

        return true
    }

    override fun hashCode(): Int {
        var result = targets.hashCode()
        result = 31 * result + values.hashCode()
        return result
    }

    override fun toString() = "MultiAssign(targets=$targets, values=$values)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitMultiAssign(this)

    override fun acceptChildren(visitor: CstVisitor) {
        targets.forEach { it.accept(visitor) }
        values.forEach { it.accept(visitor) }
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformMultiAssign(this)
}
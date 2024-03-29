package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.resolve.cache.CrResolveCache

class CstVar(
    val name: String,
    location: CstLocation? = null
) : CstNode<CstVar>(location) {
    companion object {
        val EMPTY = CstVar("")
    }

    fun copy(
        name: String = this.name,
        location: CstLocation? = this.location
    ) = CstVar(name, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstVar

        return name == other.name
    }
    override fun hashCode() = name.hashCode()

    override fun toString() = "Var($name)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitVar(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformVar(this)
}

fun CrResolveCache.newTempVar(location: CstLocation? = null) = CstVar(newTempVarName(), location)
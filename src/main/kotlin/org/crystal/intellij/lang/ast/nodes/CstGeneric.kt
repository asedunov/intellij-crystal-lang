package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstGeneric(
    val name: CstNode,
    val typeVars: List<CstNode>,
    val namedArgs: List<CstNamedArgument> = emptyList(),
    location: CstLocation? = null
) : CstNode(location) {
    fun copy(
        name: CstNode = this.name,
        typeVars: List<CstNode> = this.typeVars,
        namedArgs: List<CstNamedArgument> = this.namedArgs,
        location: CstLocation? = this.location
    ) = CstGeneric(name, typeVars, namedArgs, location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstGeneric

        if (name != other.name) return false
        if (typeVars != other.typeVars) return false
        if (namedArgs != other.namedArgs) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + typeVars.hashCode()
        result = 31 * result + namedArgs.hashCode()
        return result
    }

    override fun toString() = buildString {
        append("Generic(")
        append(name)
        append(", typeVars=$typeVars")
        if (namedArgs.isNotEmpty()) append(", namedArgs=$namedArgs")
    }

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitGeneric(this)

    override fun acceptChildren(visitor: CstVisitor) {
        name.accept(visitor)
        typeVars.forEach { it.accept(visitor) }
        namedArgs.forEach { it.accept(visitor) }
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformGeneric(this)
}
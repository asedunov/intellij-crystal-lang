package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstMacroVar(
    val name: String,
    val exps: List<CstNode<*>> = emptyList(),
    location: CstLocation? = null
) : CstNode<CstMacroVar>(location) {
    fun copy(
        name: String = this.name,
        exps: List<CstNode<*>> = this.exps,
        location: CstLocation? = this.location
    ) = CstMacroVar(name, exps, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstMacroVar

        if (name != other.name) return false
        if (exps != other.exps) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + exps.hashCode()
        return result
    }

    override fun toString() = buildString {
        append("MacroVar(")
        append(name)
        if (exps.isNotEmpty()) append(", exps=$exps")
        append(")")
    }

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitMacroVar(this)

    override fun acceptChildren(visitor: CstVisitor) {
        exps.forEach { it.accept(visitor) }
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformMacroVar(this)
}
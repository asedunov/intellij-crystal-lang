package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.util.crystal.CrSymbol
import org.crystal.intellij.util.crystal.crSymbol

class CstSymbolLiteral(
    val value: String,
    location: CstLocation? = null
) : CstNode<CstSymbolLiteral>(location), CstSimpleLiteral {
    fun copy(
        value: String = this.value,
        location: CstLocation? = this.location
    ) = CstSymbolLiteral(value, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstSymbolLiteral

        return value == other.value
    }

    override fun hashCode() = value.hashCode()

    override fun toString() = "SymbolLiteral($value)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitSymbolLiteral(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformSymbolLiteral(this)
}

val CstSymbolLiteral.crSymbol: CrSymbol
    get() = value.crSymbol
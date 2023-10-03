package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstNumberLiteral(
    val value: String,
    val kind: NumberKind = NumberKind.I32,
    location: CstLocation? = null
) : CstNode<CstNumberLiteral>(location), CstSimpleLiteral {
    constructor(
        value: Int,
        location: CstLocation? = null
    ) : this(value = value.toString(), location = location)

    enum class NumberKind {
        I8,
        U8,
        I16,
        U16,
        I32,
        U32,
        I64,
        U64,
        I128,
        U128,
        F32,
        F64;

        val spec: String
            get() = name.lowercase()
    }

    fun copy(
        value: String = this.value,
        kind: NumberKind = this.kind,
        location: CstLocation? = this.location
    ) = CstNumberLiteral(value, kind, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstNumberLiteral

        if (value.toDoubleOrNull() != other.value.toDoubleOrNull()) return false
        if (kind != other.kind) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value.toDoubleOrNull()?.hashCode() ?: 0
        result = 31 * result + kind.hashCode()
        return result
    }

    override fun toString() = buildString {
        append("NumberLiteral(")
        append(value)
        if (kind != NumberKind.I32) append(", $kind")
        append(")")
    }

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitNumberLiteral(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformNumberLiteral(this)
}
package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstPath(
    val names: List<String>,
    val isGlobal: Boolean = false,
    location: CstLocation? = null
) : CstNode<CstPath>(location) {
    companion object {
        fun global(
            name: String,
            location: CstLocation? = null
        ) = CstPath(listOf(name), true, location)

        fun global(
            names: List<String>,
            location: CstLocation? = null
        ) = CstPath(names, true, location)
    }

    fun copy(
        names: List<String> = this.names,
        isGlobal: Boolean = this.isGlobal,
        location: CstLocation? = this.location
    ) = CstPath(names, isGlobal, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstPath

        if (names != other.names) return false
        if (isGlobal != other.isGlobal) return false

        return true
    }

    override fun hashCode(): Int {
        var result = names.hashCode()
        result = 31 * result + isGlobal.hashCode()
        return result
    }

    override fun toString() = buildString {
        append("Path(")
        append(names)
        if (isGlobal) append(", isGlobal")
        append(")")
    }

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitPath(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformPath(this)
}
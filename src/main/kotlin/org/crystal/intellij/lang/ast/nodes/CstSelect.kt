package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstSelect(
    val whens: List<When>,
    val elseBranch: CstNode? = null,
    location: CstLocation? = null
) : CstNode(location) {
    data class When(
        val condition: CstNode,
        val body: CstNode
    ) {
        fun accept(visitor: CstVisitor) {
            condition.accept(visitor)
            body.accept(visitor)
        }
    }

    fun copy(
        whens: List<When> = this.whens,
        elseBranch: CstNode? = this.elseBranch,
        location: CstLocation? = this.location
    ) = CstSelect(whens, elseBranch, location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstSelect

        if (whens != other.whens) return false
        if (elseBranch != other.elseBranch) return false

        return true
    }

    override fun hashCode(): Int {
        var result = whens.hashCode()
        result = 31 * result + (elseBranch?.hashCode() ?: 0)
        return result
    }

    override fun toString() = buildString {
        append("Select(")
        append("whens=$whens")
        if (elseBranch != null) append("elseBranch=$elseBranch")
        append(")")
    }

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitSelect(this)

    override fun acceptChildren(visitor: CstVisitor) {
        whens.forEach { it.accept(visitor) }
        elseBranch?.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformSelect(this)
}
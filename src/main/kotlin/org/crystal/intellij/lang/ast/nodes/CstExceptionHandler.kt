package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstExceptionHandler(
    val body: CstNode<*> = CstNop,
    val rescues: List<CstRescue> = emptyList(),
    val elseBranch: CstNode<*>? = null,
    val ensure: CstNode<*>? = null,
    location: CstLocation? = null
) : CstNode<CstExceptionHandler>(location) {
    fun copy(
        body: CstNode<*> = this.body,
        rescues: List<CstRescue> = this.rescues,
        elseBranch: CstNode<*>? = this.elseBranch,
        ensure: CstNode<*>? = this.ensure,
        location: CstLocation? = this.location
    ) = CstExceptionHandler(body, rescues, elseBranch, ensure, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstExceptionHandler

        if (body != other.body) return false
        if (rescues != other.rescues) return false
        if (elseBranch != other.elseBranch) return false
        if (ensure != other.ensure) return false

        return true
    }

    override fun hashCode(): Int {
        var result = body.hashCode()
        result = 31 * result + rescues.hashCode()
        result = 31 * result + (elseBranch?.hashCode() ?: 0)
        result = 31 * result + (ensure?.hashCode() ?: 0)
        return result
    }

    override fun toString() = sequence {
        if (body != CstNop) yield("body=$body")
        if (rescues.isNotEmpty()) yield("rescues=$rescues")
        if (elseBranch != null) yield("else=$elseBranch")
        if (ensure != null) yield("ensure=$ensure")
    }.joinToString(prefix = "ExceptionHandler(", postfix = ")")

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitExceptionHandler(this)

    override fun acceptChildren(visitor: CstVisitor) {
        body.accept(visitor)
        rescues.forEach { it.accept(visitor) }
        elseBranch?.accept(visitor)
        ensure?.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformExceptionHandler(this)
}
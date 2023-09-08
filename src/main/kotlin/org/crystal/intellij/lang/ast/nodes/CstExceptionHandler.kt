package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstExceptionHandler(
    val body: CstNode = CstNop,
    val rescues: List<CstRescue> = emptyList(),
    val elseBranch: CstNode? = null,
    val ensure: CstNode? = null,
    location: CstLocation? = null
) : CstNode(location) {
    fun copy(
        body: CstNode = this.body,
        rescues: List<CstRescue> = this.rescues,
        elseBranch: CstNode? = this.elseBranch,
        ensure: CstNode? = this.ensure,
        location: CstLocation? = this.location
    ): CstExceptionHandler {
        return CstExceptionHandler(body, rescues, elseBranch, ensure, location)
    }

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
}
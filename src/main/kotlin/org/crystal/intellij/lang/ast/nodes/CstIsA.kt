package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstIsA(
    val receiver: CstNode,
    val arg: CstNode,
    val isNilCheck: Boolean = false,
    location: CstLocation? = null
) : CstNode(location) {
    fun copy(
        receiver: CstNode = this.receiver,
        arg: CstNode = this.arg,
        isNilCheck: Boolean = this.isNilCheck,
        location: CstLocation? = this.location
    ) = CstIsA(receiver, arg, isNilCheck, location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstIsA

        if (receiver != other.receiver) return false
        if (arg != other.arg) return false
        if (isNilCheck != other.isNilCheck) return false

        return true
    }

    override fun hashCode(): Int {
        var result = receiver.hashCode()
        result = 31 * result + arg.hashCode()
        result = 31 * result + isNilCheck.hashCode()
        return result
    }

    override fun toString() = buildString {
        append("IsA(")
        append("receiver=$receiver")
        append(", arg=$arg")
        if (isNilCheck) append(", isNilCheck")
        append(")")
    }

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitIsA(this)

    override fun acceptChildren(visitor: CstVisitor) {
        receiver.accept(visitor)
        arg.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformIsA(this)
}
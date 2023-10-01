package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstIsA(
    override val obj: CstNode<*>,
    val arg: CstNode<*>,
    val isNilCheck: Boolean = false,
    location: CstLocation? = null
) : CstNodeWithReceiver<CstIsA>(location) {
    fun copy(
        obj: CstNode<*> = this.obj,
        arg: CstNode<*> = this.arg,
        isNilCheck: Boolean = this.isNilCheck,
        location: CstLocation? = this.location
    ) = CstIsA(obj, arg, isNilCheck, location)

    override fun withReceiver(obj: CstNode<*>?) = copy(obj = obj ?: CstNop)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstIsA

        if (obj != other.obj) return false
        if (arg != other.arg) return false
        if (isNilCheck != other.isNilCheck) return false

        return true
    }

    override fun hashCode(): Int {
        var result = obj.hashCode()
        result = 31 * result + arg.hashCode()
        result = 31 * result + isNilCheck.hashCode()
        return result
    }

    override fun toString() = buildString {
        append("IsA(")
        append("receiver=$obj")
        append(", arg=$arg")
        if (isNilCheck) append(", isNilCheck")
        append(")")
    }

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitIsA(this)

    override fun acceptChildren(visitor: CstVisitor) {
        obj.accept(visitor)
        arg.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformIsA(this)
}
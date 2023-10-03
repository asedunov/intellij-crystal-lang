package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstCall(
    val obj: CstNode<*>?,
    val name: String,
    val args: List<CstNode<*>> = emptyList(),
    val block: CstBlock? = null,
    val blockArg: CstNode<*>? = null,
    val namedArgs: List<CstNamedArgument> = emptyList(),
    val isGlobal: Boolean = false,
    val hasParentheses: Boolean = false,
    location: CstLocation? = null,
    override val nameLocation: CstLocation? = null
) : CstNode<CstCall>(location) {
    constructor(
        obj: CstNode<*>?,
        name: String,
        arg: CstNode<*>,
        location: CstLocation? = null,
        nameLocation: CstLocation? = null
    ) : this(obj = obj, name = name, args = listOf(arg), location = location, nameLocation = nameLocation)

    fun copy(
        obj: CstNode<*>? = this.obj,
        name: String = this.name,
        args: List<CstNode<*>> = this.args,
        block: CstBlock? = this.block,
        blockArg: CstNode<*>? = this.blockArg,
        namedArgs: List<CstNamedArgument> = this.namedArgs,
        isGlobal: Boolean = this.isGlobal,
        hasParentheses: Boolean = this.hasParentheses,
        location: CstLocation? = this.location,
        nameLocation: CstLocation? = this.nameLocation
    ) = CstCall(obj, name, args, block, blockArg, namedArgs, isGlobal, hasParentheses, location, nameLocation)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstCall

        if (obj != other.obj) return false
        if (name != other.name) return false
        if (args != other.args) return false
        if (block != other.block) return false
        if (blockArg != other.blockArg) return false
        if (namedArgs != other.namedArgs) return false
        if (isGlobal != other.isGlobal) return false

        return true
    }

    override fun hashCode(): Int {
        var result = obj?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + args.hashCode()
        result = 31 * result + (block?.hashCode() ?: 0)
        result = 31 * result + (blockArg?.hashCode() ?: 0)
        result = 31 * result + namedArgs.hashCode()
        result = 31 * result + isGlobal.hashCode()
        return result
    }

    override fun toString() = buildString {
        append("Call(")
        if (obj != null) append("obj=$obj, ")
        append(name)
        if (args.isNotEmpty()) append(", args=$args")
        if (block != null) append(", block=$block")
        if (blockArg != null) append(", blockArg=$blockArg")
        if (namedArgs.isNotEmpty()) append(", namedArgs=$namedArgs")
        if (isGlobal) append(", isGlobal")
        append(")")
    }

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitCall(this)

    override fun acceptChildren(visitor: CstVisitor) {
        obj?.accept(visitor)
        args.forEach { it.accept(visitor) }
        namedArgs.forEach { it.accept(visitor) }
        blockArg?.accept(visitor)
        block?.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformCall(this)
}

val CstCall.isSuper: Boolean
    get() = obj == null && name == "super"

val CstCall.isPreviousDef: Boolean
    get() = obj == null && name == "previous_def"
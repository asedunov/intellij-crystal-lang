package org.crystal.intellij.lang.ast.nodes

import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstBlock(
    val args: List<CstVar> = emptyList(),
    val body: CstNode = CstNop,
    val splatIndex: Int = -1,
    val unpacks: Int2ObjectMap<CstExpressions> = Int2ObjectMaps.emptyMap(),
    location: CstLocation? = null
) : CstNode(location) {
    companion object {
        val EMPTY = CstBlock()
    }

    fun copy(
        args: List<CstVar> = this.args,
        body: CstNode = this.body,
        splatIndex: Int = this.splatIndex,
        unpacks: Int2ObjectMap<CstExpressions> = this.unpacks,
        location: CstLocation? = this.location
    )  = CstBlock(args, body, splatIndex, unpacks, location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstBlock

        if (args != other.args) return false
        if (body != other.body) return false
        if (splatIndex != other.splatIndex) return false
        if (unpacks != other.unpacks) return false

        return true
    }

    override fun hashCode(): Int {
        var result = args.hashCode()
        result = 31 * result + body.hashCode()
        result = 31 * result + splatIndex
        result = 31 * result + unpacks.hashCode()
        return result
    }

    override fun toString() = sequence {
        if (args.isNotEmpty()) yield("args=$args")
        if (body != CstNop) yield("body=$body")
        if (splatIndex >= 0) yield("splatIndex=$splatIndex")
        if (unpacks.isNotEmpty()) yield("unpacks=$unpacks")
    }.joinToString(prefix = "Block(", postfix = ")")

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitBlock(this)

    override fun acceptChildren(visitor: CstVisitor) {
        args.forEach { it.accept(visitor) }
        body.accept(visitor)
        unpacks.forEach { (_, node) -> node.accept(visitor) }
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformBlock(this)
}
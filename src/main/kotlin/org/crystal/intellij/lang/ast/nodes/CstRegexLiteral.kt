package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstRegexLiteral(
    val source: CstNode<*>,
    val options: Int = NONE,
    location: CstLocation? = null
) : CstNode<CstRegexLiteral>(location) {
    companion object {
        const val NONE        = 0
        const val IGNORE_CASE = 0x0000_0001
        const val MULTILINE   = 0x0000_0006
        const val EXTENDED    = 0x0000_0008
    }

    fun copy(
        source: CstNode<*> = this.source,
        options: Int = this.options,
        location: CstLocation? = this.location
    ) = CstRegexLiteral(source, options, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstRegexLiteral

        if (source != other.source) return false
        if (options != other.options) return false

        return true
    }

    override fun hashCode(): Int {
        var result = source.hashCode()
        result = 31 * result + options
        return result
    }

    override fun toString() = "RegexLiteral($source, $options)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitRegexLiteral(this)

    override fun acceptChildren(visitor: CstVisitor) {
        source.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformRegexLiteral(this)
}
package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.util.crystal.CrChar
import org.crystal.intellij.util.crystal.crChar

class CstCharLiteral(
    val value: Int,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstCharLiteral

        return value == other.value
    }

    override fun hashCode() = value

    override fun toString() = buildString {
        append("CharLiteral(").appendCodePoint(value).append(")")
    }

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitCharLiteral(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformCharLiteral(this)
}

val CstCharLiteral.crChar: CrChar
    get() = value.crChar
package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.util.crystal.CrString
import org.crystal.intellij.util.crystal.crString

class CstStringLiteral(
    val value: String,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstStringLiteral

        return value == other.value
    }

    override fun hashCode() = value.hashCode()

    override fun toString() = "StringLiteral($value)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitStringLiteral(this)
}

val CstStringLiteral.crString: CrString
    get() = value.crString
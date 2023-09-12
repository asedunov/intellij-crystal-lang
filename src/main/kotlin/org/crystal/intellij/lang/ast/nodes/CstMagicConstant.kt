package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstVisitor

sealed class CstMagicConstant(
    location: CstLocation? = null
) : CstNode(location) {
    class Dir(location: CstLocation? = null) : CstMagicConstant(location)

    class File(location: CstLocation? = null) : CstMagicConstant(location)

    class Line(location: CstLocation? = null) : CstMagicConstant(location)

    class EndLine(location: CstLocation? = null) : CstMagicConstant(location)

    override fun equals(other: Any?) = this === other || javaClass == other?.javaClass

    override fun hashCode() = javaClass.hashCode()

    override fun toString() = strippedClassName

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitMagicConstant(this)
}
package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.lexer.*

sealed class CstMagicConstant(
    val tokenType: CrystalTokenType,
    location: CstLocation? = null
) : CstNode(location) {
    class Dir(location: CstLocation? = null) : CstMagicConstant(CR_DIR_, location)

    class File(location: CstLocation? = null) : CstMagicConstant(CR_FILE_, location)

    class Line(location: CstLocation? = null) : CstMagicConstant(CR_LINE_, location)

    class EndLine(location: CstLocation? = null) : CstMagicConstant(CR_END_LINE_, location)

    override fun equals(other: Any?) = this === other || javaClass == other?.javaClass

    override fun hashCode() = javaClass.hashCode()

    override fun toString() = strippedClassName

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitMagicConstant(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformMagicConstant(this)
}
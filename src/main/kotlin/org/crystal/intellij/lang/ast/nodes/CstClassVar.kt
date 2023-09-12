package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstClassVar(
    val name: String,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstClassVar

        return name == other.name
    }

    override fun hashCode() = name.hashCode()

    override fun toString() = "ClassVar($name)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitClassVar(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformClassVar(this)
}
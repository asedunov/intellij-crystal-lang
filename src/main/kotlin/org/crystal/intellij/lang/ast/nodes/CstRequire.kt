package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstRequire(
    val path: String,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstRequire

        return path == other.path
    }

    override fun hashCode() = path.hashCode()

    override fun toString() = "Require($path)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitRequire(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformRequire(this)
}
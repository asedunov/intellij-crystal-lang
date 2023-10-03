package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstAnnotationDef(
    val name: CstPath,
    location: CstLocation? = null,
    override val nameLocation: CstLocation? = null
) : CstNode<CstAnnotationDef>(location) {
    fun copy(
        name: CstPath = this.name,
        location: CstLocation? = this.location,
        nameLocation: CstLocation? = this.nameLocation
    ) = CstAnnotationDef(name, location, nameLocation)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstAnnotationDef

        return name == other.name
    }

    override fun hashCode() = name.hashCode()

    override fun toString() = "AnnotationDef($name)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitAnnotationDef(this)

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformAnnotationDef(this)
}
package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstAnnotationDef(
    val name: CstPath,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstAnnotationDef

        return name == other.name
    }

    override fun hashCode() = name.hashCode()

    override fun toString() = "AnnotationDef($name)"
}
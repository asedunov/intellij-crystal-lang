package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstSelf(
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?) = this === other || javaClass == other?.javaClass

    override fun hashCode() = javaClass.hashCode()

    override fun toString() = "Self"
}
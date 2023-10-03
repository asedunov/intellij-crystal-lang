package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstTypeDeclaration(
    val variable: CstNode<*>,
    val type: CstNode<*>,
    val value: CstNode<*>? = null,
    location: CstLocation? = null
) : CstNode<CstTypeDeclaration>(location) {
    fun copy(
        variable: CstNode<*> = this.variable,
        type: CstNode<*> = this.type,
        value: CstNode<*>? = this.value,
        location: CstLocation? = this.location
    ) = CstTypeDeclaration(variable, type, value, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstTypeDeclaration

        if (variable != other.variable) return false
        if (type != other.type) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = variable.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + (value?.hashCode() ?: 0)
        return result
    }

    override fun toString() = buildString {
        append("TypeDeclaration(")
        append(variable)
        append(", type=$type")
        if (value != null) append(", value=$value")
        append(")")
    }

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitTypeDeclaration(this)

    override fun acceptChildren(visitor: CstVisitor) {
        variable.accept(visitor)
        type.accept(visitor)
        value?.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformTypeDeclaration(this)
}
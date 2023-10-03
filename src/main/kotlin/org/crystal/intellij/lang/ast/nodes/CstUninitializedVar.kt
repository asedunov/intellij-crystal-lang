package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstUninitializedVar(
    val variable: CstNode<*>,
    val declaredType: CstNode<*>,
    location: CstLocation? = null
) : CstNode<CstUninitializedVar>(location) {
    fun copy(
        variable: CstNode<*> = this.variable,
        declaredType: CstNode<*> = this.declaredType,
        location: CstLocation? = this.location
    ) = CstUninitializedVar(variable, declaredType, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstUninitializedVar

        if (variable != other.variable) return false
        if (declaredType != other.declaredType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = variable.hashCode()
        result = 31 * result + declaredType.hashCode()
        return result
    }

    override fun toString() = "UninitializedVar(variable=$variable, declaredType=$declaredType)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitUninitializedVar(this)

    override fun acceptChildren(visitor: CstVisitor) {
        variable.accept(visitor)
        declaredType.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformUninitializedVar(this)
}
package org.crystal.intellij.psi

sealed interface CrDefinition : CrExpression, CrNamedElement {
    override fun accept(visitor: CrVisitor) = visitor.visitDefinition(this)
}
package org.crystal.intellij.lang.psi

sealed interface CrDefinition : CrExpression, CrNamedElement {
    override fun accept(visitor: CrVisitor) = visitor.visitDefinition(this)
}
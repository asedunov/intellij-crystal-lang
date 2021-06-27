package org.crystal.intellij.psi

interface CrExpression : CrElement {
    override fun accept(visitor: CrVisitor) = visitor.visitExpression(this)
}
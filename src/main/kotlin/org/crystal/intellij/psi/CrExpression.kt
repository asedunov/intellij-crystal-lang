package org.crystal.intellij.psi

interface CrExpression : CrElement, CrCallArgument {
    override fun accept(visitor: CrVisitor) = visitor.visitExpression(this)
}
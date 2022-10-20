package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrNamedTupleEntry(node: ASTNode) : CrElementImpl(node), CrNamedElement, CrSimpleNameElementHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitNamedTupleEntry(this)

    val expression: CrExpression?
        get() = childOfType()
}
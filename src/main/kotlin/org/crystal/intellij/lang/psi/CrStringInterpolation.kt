package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrStringInterpolation(node: ASTNode) : CrElementImpl(node), CrStringValueHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitStringInterpolation(this)

    override val stringValue: String?
        get() = null

    val expression: CrExpression?
        get() = childOfType()
}
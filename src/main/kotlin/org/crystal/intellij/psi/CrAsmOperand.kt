package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrAsmOperand(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitAsmOperand(this)

    private val label: CrStringLiteralExpression?
        get() = childOfType()

    val argument: CrExpression?
        get() = label?.nextSiblingOfType()
}
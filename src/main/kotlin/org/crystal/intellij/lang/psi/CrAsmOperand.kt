package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrAsmOperand(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitAsmOperand(this)

    val label: CrStringLiteralExpression?
        get() = childOfType()

    val argument: CrExpression?
        get() = label?.nextSiblingOfType()
}
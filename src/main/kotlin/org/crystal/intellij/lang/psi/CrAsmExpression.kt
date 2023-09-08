package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrAsmExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitAsmExpression(this)

    val label: CrStringLiteralExpression?
        get() = childOfType()

    val inputs: CrAsmInList?
        get() = childOfType()

    val outputs: CrAsmOutList?
        get() = childOfType()

    val clobberList: CrAsmClobberList?
        get() = childOfType()

    val optionList: CrAsmOptionsList?
        get() = childOfType()
}
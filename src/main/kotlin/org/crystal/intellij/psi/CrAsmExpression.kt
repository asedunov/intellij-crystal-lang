package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrAsmExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitAsmExpression(this)
}
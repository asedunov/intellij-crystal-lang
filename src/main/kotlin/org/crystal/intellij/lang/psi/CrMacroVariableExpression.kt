package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable

class CrMacroVariableExpression(
    node: ASTNode
) : CrExpressionImpl(node), CrSimpleNameElementHolder, CrMacroLiteralElement {
    override fun accept(visitor: CrVisitor) = visitor.visitMacroVariableExpression(this)

    val expressions: JBIterable<CrExpression>
        get() = childrenOfType()
}
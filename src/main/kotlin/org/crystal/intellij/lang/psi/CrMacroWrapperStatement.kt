package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable

class CrMacroWrapperStatement(node: ASTNode) : CrExpressionImpl(node), CrMacroLiteralElement {
    override fun accept(visitor: CrVisitor) = visitor.visitMacroWrapperStatement(this)

    val expressions: JBIterable<CrExpression>
        get() = childrenOfType()
}
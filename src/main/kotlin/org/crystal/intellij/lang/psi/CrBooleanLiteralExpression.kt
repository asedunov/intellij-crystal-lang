package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.util.elementType
import org.crystal.intellij.lang.lexer.CR_TRUE

class CrBooleanLiteralExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitBooleanLiteralExpression(this)

    val isTrue: Boolean
        get() = firstChild.elementType == CR_TRUE
}
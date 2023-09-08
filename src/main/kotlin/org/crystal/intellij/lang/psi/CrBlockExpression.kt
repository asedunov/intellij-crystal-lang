package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.util.elementType
import com.intellij.util.containers.JBIterable
import org.crystal.intellij.lang.lexer.CR_BEGIN

class CrBlockExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitBlockExpression(this)

    val isBeginEnd: Boolean
        get() = firstChild.elementType == CR_BEGIN

    val parameterList: CrBlockParameterList?
        get() = childOfType()

    val expressions: JBIterable<CrExpression>
        get() = childrenOfType()

    val exceptionHandler: CrExceptionHandler?
        get() = childOfType()
}
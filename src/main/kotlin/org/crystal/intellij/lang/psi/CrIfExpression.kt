package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.util.elementType
import org.crystal.intellij.lang.lexer.CR_ELSIF

class CrIfExpression(node: ASTNode) : CrIfUnlessExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitIfExpression(this)

    val isElsif: Boolean
        get() = firstChild.elementType == CR_ELSIF

    val elsifExpression: CrIfExpression?
        get() = childOfType()
}
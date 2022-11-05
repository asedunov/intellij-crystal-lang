package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable

class CrNamedTupleExpression(node: ASTNode) : CrExpressionImpl(node), CrLiteralExpression {
    override fun accept(visitor: CrVisitor) = visitor.visitNamedTupleExpression(this)

    val constructorType: CrTypeElement<*>?
        get() = firstChild as? CrTypeElement<*>

    val entries: JBIterable<CrNamedTupleEntry>
        get() = childrenOfType()
}
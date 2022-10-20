package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_EXPRESSION_TYPE
import org.crystal.intellij.stubs.api.CrTypeStub

class CrExpressionTypeElement : CrTypeElement<CrExpressionTypeElement> {
    constructor(stub: CrTypeStub<CrExpressionTypeElement>) : super(stub, CR_EXPRESSION_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitExpressionType(this)

    val expression: CrExpression?
        get() = childOfType()
}
package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable
import org.crystal.intellij.lang.parser.CR_EXPRESSION_TYPE
import org.crystal.intellij.lang.stubs.api.CrTypeStub

class CrExpressionTypeElement : CrTypeElement<CrExpressionTypeElement> {
    constructor(stub: CrTypeStub<CrExpressionTypeElement>) : super(stub, CR_EXPRESSION_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitExpressionType(this)

    val expressions: JBIterable<CrExpression>
        get() = childrenOfType()
}
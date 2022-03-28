package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_REQUIRE_EXPRESSION
import org.crystal.intellij.stubs.api.CrRequireStub

class CrRequireExpression : CrStubbedElementImpl<CrRequireStub>, CrExpression {
    constructor(stub: CrRequireStub) : super(stub, CR_REQUIRE_EXPRESSION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitRequireExpression(this)

    val path: CrStringLiteralExpression?
        get() = childOfType()
}
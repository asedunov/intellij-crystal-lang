package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_EXTEND_EXPRESSION
import org.crystal.intellij.stubs.api.CrExtendStub

class CrExtendExpression : CrStubbedElementImpl<CrExtendStub>, CrIncludeLikeExpression {
    constructor(stub: CrExtendStub) : super(stub, CR_EXTEND_EXPRESSION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitExtendExpression(this)
}
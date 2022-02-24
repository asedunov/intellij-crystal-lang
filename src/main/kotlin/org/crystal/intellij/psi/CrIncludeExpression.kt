package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_INCLUDE_EXPRESSION
import org.crystal.intellij.stubs.api.CrIncludeStub

class CrIncludeExpression : CrStubbedElementImpl<CrIncludeStub>, CrIncludeLikeExpression {
    constructor(stub: CrIncludeStub) : super(stub, CR_INCLUDE_EXPRESSION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitIncludeExpression(this)
}
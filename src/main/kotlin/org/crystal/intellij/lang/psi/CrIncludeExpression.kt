package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lang.parser.CR_INCLUDE_EXPRESSION
import org.crystal.intellij.lang.stubs.api.CrIncludeStub

class CrIncludeExpression : CrStubbedElementImpl<CrIncludeStub>, CrIncludeLikeExpression {
    constructor(stub: CrIncludeStub) : super(stub, CR_INCLUDE_EXPRESSION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitIncludeExpression(this)
}
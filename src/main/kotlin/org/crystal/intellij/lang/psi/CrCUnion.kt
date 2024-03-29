package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lang.parser.CR_C_UNION_DEFINITION
import org.crystal.intellij.lang.stubs.api.CrCUnionStub

class CrCUnion : CrCStructLikeDefinition<CrCUnion, CrCUnionStub> {
    constructor(stub: CrCUnionStub) : super(stub, CR_C_UNION_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitCUnion(this)
}
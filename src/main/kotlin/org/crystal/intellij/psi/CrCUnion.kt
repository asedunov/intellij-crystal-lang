package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_C_UNION_DEFINITION
import org.crystal.intellij.stubs.api.CrCUnionStub

class CrCUnion : CrModuleLikeDefinition<CrCUnion, CrCUnionStub> {
    constructor(stub: CrCUnionStub) : super(stub, CR_C_UNION_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitCUnion(this)
}
package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lang.parser.CR_C_STRUCT_DEFINITION
import org.crystal.intellij.lang.stubs.api.CrCStructStub

class CrCStruct : CrCStructLikeDefinition<CrCStruct, CrCStructStub> {
    constructor(stub: CrCStructStub) : super(stub, CR_C_STRUCT_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitCStruct(this)
}
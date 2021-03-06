package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_C_STRUCT_DEFINITION
import org.crystal.intellij.stubs.api.CrCStructStub

class CrCStruct : CrModuleLikeDefinition<CrCStruct, CrCStructStub> {
    constructor(stub: CrCStructStub) : super(stub, CR_C_STRUCT_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitCStruct(this)
}
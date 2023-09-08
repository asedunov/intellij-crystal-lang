package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lang.parser.CR_STRUCT_DEFINITION
import org.crystal.intellij.lang.stubs.api.CrStructStub

class CrStruct : CrModuleLikeDefinition<CrStruct, CrStructStub>, CrTypeParameterHolder, CrSuperTypeAware {
    constructor(stub: CrStructStub) : super(stub, CR_STRUCT_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitStruct(this)
}
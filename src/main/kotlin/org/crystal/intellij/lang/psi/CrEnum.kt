package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lang.parser.CR_ENUM_DEFINITION
import org.crystal.intellij.lang.stubs.api.CrEnumStub

class CrEnum : CrModuleLikeDefinition<CrEnum, CrEnumStub>, CrSuperTypeAware {
    constructor(stub: CrEnumStub) : super(stub, CR_ENUM_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitEnum(this)
}
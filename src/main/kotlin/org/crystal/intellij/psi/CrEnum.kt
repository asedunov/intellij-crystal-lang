package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_ENUM_DEFINITION
import org.crystal.intellij.stubs.api.CrEnumStub

class CrEnum :
    CrDefinitionWithFqNameImpl<CrEnum, CrEnumStub>,
    CrDefinitionWithBody,
    CrTypeDefinition
{
    constructor(stub: CrEnumStub) : super(stub, CR_ENUM_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitEnum(this)
}
package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lang.parser.CR_ENUM_CONSTANT_DEFINITION
import org.crystal.intellij.lang.stubs.api.CrEnumConstantStub

class CrEnumConstant : CrDefinitionWithFqNameImpl<CrEnumConstant, CrEnumConstantStub>,
    CrSimpleNameElementHolder,
    CrDefinitionWithInitializer {
    constructor(stub: CrEnumConstantStub) : super(stub, CR_ENUM_CONSTANT_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitEnumConstant(this)
}
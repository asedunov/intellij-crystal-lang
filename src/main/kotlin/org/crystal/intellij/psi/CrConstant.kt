package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_CONSTANT_DEFINITION
import org.crystal.intellij.stubs.api.CrConstantStub

class CrConstant : CrDefinitionWithFqNameImpl<CrConstant, CrConstantStub>, CrDefinitionWithInitializer, CrPathNameElementHolder {
    constructor(stub: CrConstantStub) : super(stub, CR_CONSTANT_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitConstant(this)
}
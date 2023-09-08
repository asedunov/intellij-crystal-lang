package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lang.parser.CR_MODULE_DEFINITION
import org.crystal.intellij.lang.stubs.api.CrModuleStub

class CrModule : CrModuleLikeDefinition<CrModule, CrModuleStub>, CrTypeParameterHolder {
    constructor(stub: CrModuleStub) : super(stub, CR_MODULE_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitModule(this)
}
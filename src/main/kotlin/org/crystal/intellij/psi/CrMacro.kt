package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_MACRO_DEFINITION
import org.crystal.intellij.stubs.api.CrMacroStub

class CrMacro : CrDefinitionWithFqNameImpl<CrMacro, CrMacroStub>, CrSimpleNameElementHolder, CrFunctionLikeDefinition {
    constructor(stub: CrMacroStub) : super(stub, CR_MACRO_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitMacro(this)
}
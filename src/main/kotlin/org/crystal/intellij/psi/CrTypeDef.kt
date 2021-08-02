package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_TYPE_DEFINITION
import org.crystal.intellij.stubs.api.CrTypeDefStub

class CrTypeDef : CrDefinitionWithFqNameImpl<CrTypeDef, CrTypeDefStub>, CrAliasLikeDefinition, CrSimpleNameElementHolder {
    constructor(stub: CrTypeDefStub) : super(stub, CR_TYPE_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitTypeDef(this)
}
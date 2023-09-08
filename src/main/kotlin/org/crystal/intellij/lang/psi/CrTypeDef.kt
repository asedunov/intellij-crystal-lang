package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lang.parser.CR_TYPE_DEFINITION
import org.crystal.intellij.lang.stubs.api.CrTypeDefStub

class CrTypeDef : CrDefinitionWithFqNameImpl<CrTypeDef, CrTypeDefStub>, CrAliasLikeDefinition {
    constructor(stub: CrTypeDefStub) : super(stub, CR_TYPE_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitTypeDef(this)
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_ALIAS_DEFINITION
import org.crystal.intellij.stubs.api.CrAliasStub

class CrAlias : CrDefinitionWithFqNameImpl<CrAlias, CrAliasStub>, CrAliasLikeDefinition {
    constructor(stub: CrAliasStub) : super(stub, CR_ALIAS_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitAlias(this)
}
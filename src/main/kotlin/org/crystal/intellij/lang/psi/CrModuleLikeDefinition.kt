package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.IStubElementType
import org.crystal.intellij.lang.stubs.api.CrDefinitionWithFqNameStub

sealed class CrModuleLikeDefinition<
        Psi : CrModuleLikeDefinition<Psi, Stub>,
        Stub : CrDefinitionWithFqNameStub<Psi>
        > : CrDefinitionWithFqNameImpl<Psi, Stub>, CrDefinitionWithBody, CrTypeDefinition {
    constructor(stub: Stub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    constructor(node: ASTNode) : super(node)
}
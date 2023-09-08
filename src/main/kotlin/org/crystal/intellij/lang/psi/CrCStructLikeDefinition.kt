package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.IStubElementType
import org.crystal.intellij.lang.stubs.api.CrDefinitionWithFqNameStub

sealed class CrCStructLikeDefinition<
        Psi : CrCStructLikeDefinition<Psi, Stub>,
        Stub : CrDefinitionWithFqNameStub<Psi>
        > : CrModuleLikeDefinition<Psi, Stub> {
    constructor(stub: Stub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    constructor(node: ASTNode) : super(node)
}
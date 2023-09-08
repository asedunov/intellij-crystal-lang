package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.IStubElementType
import org.crystal.intellij.lang.stubs.api.CrDefinitionWithFqNameStub

sealed class CrDefinitionWithFqNameImpl<
        Psi : CrDefinitionWithFqName,
        Stub : CrDefinitionWithFqNameStub<Psi>
> : CrDefinitionImpl<Stub>, CrDefinitionWithFqName {
    constructor(stub: Stub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    constructor(node: ASTNode) : super(node)

    override val isLocal: Boolean
        get() = greenStub == null && super.isLocal

    override val visibility: CrVisibility?
        get() {
            greenStub?.let { return it.visibility }
            return super.visibility
        }

    override val isAbstract: Boolean
        get() {
            val stub = greenStub
            return stub != null && stub.isAbstract || super.isAbstract
        }
}
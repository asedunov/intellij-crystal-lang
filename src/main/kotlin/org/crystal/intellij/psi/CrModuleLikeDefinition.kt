package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.IStubElementType
import com.intellij.util.containers.JBIterable
import org.crystal.intellij.stubs.api.CrDefinitionWithFqNameStub

sealed class CrModuleLikeDefinition<
        Psi : CrModuleLikeDefinition<Psi, Stub>,
        Stub : CrDefinitionWithFqNameStub<Psi>
        > : CrDefinitionWithFqNameImpl<Psi, Stub>, CrDefinitionWithBody, CrTypeDefinition {
    constructor(stub: Stub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    constructor(node: ASTNode) : super(node)

    val includes: JBIterable<CrIncludeExpression>
        get() {
            return if (stub != null) stubChildrenOfType() else body?.childrenOfType() ?: JBIterable.empty()
        }
}
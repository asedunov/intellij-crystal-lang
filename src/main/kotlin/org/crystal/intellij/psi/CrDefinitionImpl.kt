package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.IStubElementType
import org.crystal.intellij.presentation.CrystalDefaultDefinitionPresentation
import org.crystal.intellij.stubs.api.CrDefinitionStub

sealed class CrDefinitionImpl<Stub : CrDefinitionStub<*>> : CrStubbedElementImpl<Stub>, CrDefinition, CrNamedElement {
    constructor(stub: Stub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    constructor(node: ASTNode) : super(node)

    override fun getPresentation() = CrystalDefaultDefinitionPresentation(this)
}
package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.IStubElementType
import org.crystal.intellij.lang.stubs.api.CrTypeStub

sealed class CrTypeElement<Psi : CrTypeElement<Psi>> :
    CrStubbedElementImpl<CrTypeStub<Psi>>, CrMethodReceiver, CrTypeArgument {
    constructor(stub: CrTypeStub<Psi>, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitType(this)
}
package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.IStubElementType
import org.crystal.intellij.stubs.api.CrTypeStub

sealed class CrType<Psi : CrType<Psi>> : CrStubbedElementImpl<CrTypeStub<Psi>>, CrMethodReceiver {
    constructor(stub: CrTypeStub<Psi>, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitType(this)
}
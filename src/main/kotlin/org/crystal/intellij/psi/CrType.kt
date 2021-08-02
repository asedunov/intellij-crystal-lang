package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.IStubElementType
import org.crystal.intellij.stubs.api.CrTypeStub

sealed class CrType : CrStubbedElementImpl<CrTypeStub>, CrMethodReceiver {
    constructor(stub: CrTypeStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitType(this)
}
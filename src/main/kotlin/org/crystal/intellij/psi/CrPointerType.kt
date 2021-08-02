package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_POINTER_TYPE
import org.crystal.intellij.stubs.api.CrTypeStub

class CrPointerType : CrType {
    constructor(stub: CrTypeStub) : super(stub, CR_POINTER_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitPointerType(this)

    val innerType: CrType?
        get() = stubChildOfType()
}
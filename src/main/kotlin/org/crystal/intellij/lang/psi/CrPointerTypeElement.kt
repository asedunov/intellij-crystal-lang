package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lang.parser.CR_POINTER_TYPE
import org.crystal.intellij.lang.stubs.api.CrTypeStub

class CrPointerTypeElement : CrTypeElement<CrPointerTypeElement> {
    constructor(stub: CrTypeStub<CrPointerTypeElement>) : super(stub, CR_POINTER_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitPointerType(this)

    val innerType: CrTypeElement<*>?
        get() = stubChildOfType()
}